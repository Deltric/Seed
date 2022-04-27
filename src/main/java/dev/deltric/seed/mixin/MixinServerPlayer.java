package dev.deltric.seed.mixin;

import com.mojang.authlib.GameProfile;
import dev.deltric.seed.api.callbacks.TransactionCallback;
import dev.deltric.seed.api.economy.Currency;
import dev.deltric.seed.api.economy.CurrencyHolder;
import dev.deltric.seed.api.economy.Transaction;
import dev.deltric.seed.api.economy.TransactionResult;
import dev.deltric.seed.api.economy.TransactionType;
import dev.deltric.seed.util.adapters.DataKeys;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Mixin(ServerPlayerEntity.class)
abstract class MixinServerPlayer extends PlayerEntity implements CurrencyHolder {
    private Map<Identifier, Integer> walletMap = new HashMap<>();

    public MixinServerPlayer(World world, BlockPos pos, float yaw, GameProfile profile)
    {
        super(world, pos, yaw, profile);
    }

    @Inject(at = @At("HEAD"), method = "readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V")
    public void readCustomData(NbtCompound nbt, CallbackInfo info) {
        if(nbt.contains(DataKeys.SEED_WALLET)) {
            NbtCompound compound = nbt.getCompound(DataKeys.SEED_WALLET);
            compound.getKeys().forEach(key ->
                    this.walletMap.put(new Identifier(key), compound.getInt(key)));
        }
    }

    @Inject(at = @At("HEAD"), method = "writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V")
    public void writeCustomData(NbtCompound nbt, CallbackInfo info) {
        NbtCompound compound = new NbtCompound();
        walletMap.forEach((id, balance) -> compound.putInt(id.toString(), balance));
        nbt.put(DataKeys.SEED_WALLET, compound);
    }

    @Inject(at = @At("HEAD"), method = "copyFrom")
    public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo info) {
        if(oldPlayer instanceof CurrencyHolder oldHolder) {
            this.walletMap = oldHolder.getWalletMap();
        }
    }

    @NotNull
    @Override
    public Map<Identifier, Integer> getWalletMap() {
        return this.walletMap;
    }

    @NotNull
    @Override
    public Transaction deposit(@NotNull Currency currency, int amount) {
        if(!this.walletMap.containsKey(currency.getId())) {
            this.walletMap.put(currency.getId(), 0);
        }

        Integer currentBalance = this.walletMap.get(currency.getId());

        if(amount == 0) {
            return new Transaction(this.getUuid(), currency, TransactionType.DEPOSIT,
                    TransactionResult.NO_MODIFICATION, currentBalance, currentBalance, 0);
        }

        BigDecimal sum = new BigDecimal((long) currentBalance + amount);
        if(sum.longValue() > Integer.MAX_VALUE) {
            return new Transaction(this.getUuid(), currency, TransactionType.DEPOSIT,
                    TransactionResult.BALANCE_OVERFLOW, currentBalance, currentBalance, amount);
        }

        Transaction postTransaction = new Transaction(this.getUuid(), currency, TransactionType.DEPOSIT,
                TransactionResult.SUCCESS, currentBalance, sum.intValue(), amount);

        // Run the transaction through a transaction event
        ActionResult result = TransactionCallback.Companion.getEVENT().invoker().invoke(postTransaction);
        if(result == ActionResult.FAIL) {
            return new Transaction(this.getUuid(), currency, TransactionType.DEPOSIT,
                    TransactionResult.CALLBACK_CANCELED, currentBalance, currentBalance, amount);
        }
        this.walletMap.put(currency.getId(), postTransaction.getFinalBalance());
        return postTransaction;
    }

    @NotNull
    @Override
    public Transaction withdraw(@NotNull Currency currency, int amount) {
        if(!this.walletMap.containsKey(currency.getId())) {
            this.walletMap.put(currency.getId(), 0);
        }

        Integer currentBalance = this.walletMap.get(currency.getId());

        if(amount == 0) {
            return new Transaction(this.getUuid(), currency, TransactionType.WITHDRAW,
                    TransactionResult.NO_MODIFICATION, currentBalance, currentBalance, 0);
        }

        BigDecimal difference = new BigDecimal((long) currentBalance - amount);
        if(difference.longValue() < 0) {
            return new Transaction(this.getUuid(), currency, TransactionType.WITHDRAW,
                    TransactionResult.INSUFFICIENT_BALANCE, currentBalance, currentBalance, amount);
        }

        Transaction postTransaction = new Transaction(this.getUuid(), currency, TransactionType.WITHDRAW,
                TransactionResult.SUCCESS, currentBalance, difference.intValue(), amount);

        // Run the transaction through a transaction event
        ActionResult result = TransactionCallback.Companion.getEVENT().invoker().invoke(postTransaction);
        if(result == ActionResult.FAIL) {
            return new Transaction(this.getUuid(), currency, TransactionType.WITHDRAW,
                    TransactionResult.CALLBACK_CANCELED, currentBalance, currentBalance, amount);
        }
        this.walletMap.put(currency.getId(), postTransaction.getFinalBalance());
        return postTransaction;
    }

    @NotNull
    @Override
    public Transaction set(@NotNull Currency currency, int amount) {
        if(!this.walletMap.containsKey(currency.getId())) {
            this.walletMap.put(currency.getId(), 0);
        }

        Integer currentBalance = this.walletMap.get(currency.getId());
        Transaction postTransaction = new Transaction(this.getUuid(), currency, TransactionType.SET,
                TransactionResult.SUCCESS, currentBalance, amount, amount);

        // Run the transaction through a transaction event
        ActionResult result = TransactionCallback.Companion.getEVENT().invoker().invoke(postTransaction);
        if(result == ActionResult.FAIL) {
            return new Transaction(this.getUuid(), currency, TransactionType.SET,
                    TransactionResult.CALLBACK_CANCELED, currentBalance, currentBalance, amount);
        }
        return postTransaction;
    }

    @Override
    public int getBalance(@NotNull Currency currency)
    {
        return this.walletMap.getOrDefault(currency.getId(), 0);
    }
}
