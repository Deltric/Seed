package dev.deltric.seed.util.adapters

import com.google.gson.*
import net.minecraft.util.Identifier
import java.lang.reflect.Type

/**
 * String adapter for Minecraft ids
 *
 * @author Deltric
 * @since 4/23/2022
 */
object IdentifierAdapter: JsonSerializer<Identifier>, JsonDeserializer<Identifier> {
    override fun serialize(src: Identifier, typeOfSrc: Type, context: JsonSerializationContext) = JsonPrimitive(src.toString())
    override fun deserialize(json: JsonElement, type: Type, ctx: JsonDeserializationContext) = Identifier(json.asString)
}