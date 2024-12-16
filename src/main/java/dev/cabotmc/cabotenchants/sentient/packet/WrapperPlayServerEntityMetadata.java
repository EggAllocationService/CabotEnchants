package dev.cabotmc.cabotenchants.sentient.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.List;

public class WrapperPlayServerEntityMetadata
        extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_METADATA;

    public WrapperPlayServerEntityMetadata() {
        super(new PacketContainer(TYPE), TYPE);

        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerEntityMetadata(List<WrappedDataValue> metadata, int entityId) {
        this();

        this.setMetadata(metadata);
        this.setEntityId(entityId);
    }

    public int getEntityId() {
        return this.handle.getIntegers().read(0);
    }

    public void setEntityId(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public Entity getEntity(World world) {
        return this.handle.getEntityModifier(world).read(0);
    }

    public Entity getEntity(PacketEvent event) {
        return this.getEntity(event.getPlayer().getWorld());
    }

    public List<WrappedDataValue> getMetadata() {
        return this.handle.getDataValueCollectionModifier().read(0);
    }

    public void setMetadata(List<WrappedDataValue> value) {
        this.handle.getDataValueCollectionModifier().write(0, value);
    }
}