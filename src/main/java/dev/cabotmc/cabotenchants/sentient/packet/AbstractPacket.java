package dev.cabotmc.cabotenchants.sentient.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;

public abstract class AbstractPacket {
    PacketContainer handle;

    protected AbstractPacket(PacketContainer handle, PacketType type) {
        Preconditions.checkNotNull(handle, "packet handle cannot be null");
        Preconditions.checkArgument(Objects.equal(handle.getType(), type), handle.getHandle() + " is not a packet of type " + type);

        this.handle = handle;
    }

    public PacketContainer getHandle() {
        return this.handle;
    }

    public void sendPacket(Iterable<Player> recipients) {
        recipients.forEach(this::sendPacket);
    }

    public void sendPacket(Player... recipients) {
        if (recipients == null) {
            return;
        }

        for (Player receiver : recipients) {
            if (receiver == null || !receiver.isOnline()) {
                continue;
            }

            ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, this.handle);
        }
    }
}