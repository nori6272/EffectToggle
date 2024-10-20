package example.examplemod.network

import example.examplemod.utils.EffectOptions
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.effect.MobEffectInstance
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class EffectSettingPacket(val effectId: Int, val setting: EffectOptions) {
    fun encode(buf: FriendlyByteBuf) {
        buf.writeInt(effectId)
        buf.writeEnum(setting)
    }

    companion object {
        fun decode(buf: FriendlyByteBuf): EffectSettingPacket {
            val effectId = buf.readInt()
            val setting = buf.readEnum(EffectOptions::class.java)
            return EffectSettingPacket(effectId, setting)
        }
    }

    fun handle(ctx: Supplier<NetworkEvent.Context>): Boolean {
        ctx.get().enqueueWork {
            val player = ctx.get().sender
            if (player != null) {
                val effect = BuiltInRegistries.MOB_EFFECT.byId(effectId)
                if (effect != null) {
                    when (setting) {
                        EffectOptions.DISABLED -> player.removeEffect(effect)
                        EffectOptions.DEFAULT -> {
                            // デフォルトの場合は何もしない（通常の動作を維持）
                        }
                        EffectOptions.PERSISTENT -> {
                            player.addEffect(MobEffectInstance(effect, Int.MAX_VALUE, 0, false, false))
                        }
                    }
                }
            }
        }
        return true
    }
}