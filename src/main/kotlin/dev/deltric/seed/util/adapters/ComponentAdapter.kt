package dev.deltric.seed.util.adapters

import com.google.gson.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import java.lang.reflect.Type

/**
 * String adapter for Kyori adventure text components,
 * with the use of MiniMessage.
 *
 * @author Deltric
 * @since 4/23/2022
 */
object ComponentAdapter: JsonSerializer<Component>, JsonDeserializer<Component> {
    override fun serialize(src: Component, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val mm = MiniMessage.miniMessage();
        return JsonPrimitive(mm.serialize(src))
    }
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Component {
        val mm = MiniMessage.miniMessage();
        return mm.deserialize(json.asString)
    }
}