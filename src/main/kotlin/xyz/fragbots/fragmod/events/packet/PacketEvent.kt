package xyz.fragbots.fragmod.events.packet

import net.minecraft.network.Packet
import net.minecraftforge.fml.common.eventhandler.Cancelable
import net.minecraftforge.fml.common.eventhandler.Event

@Cancelable
open class PacketEvent(packet: Packet<*>) : Event() {

    private var packet: Packet<*>

    fun getPacket(): Packet<*> {
        return packet
    }

    fun setPacket(packet: Packet<*>) {
        this.packet = packet
    }

    class Outgoing(packetIn: Packet<*>) : PacketEvent(packetIn)
    class Incoming(packetIn: Packet<*>) : PacketEvent(packetIn)

    init {
        this.packet = packet
    }
}
