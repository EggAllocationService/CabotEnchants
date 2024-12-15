package dev.cabotmc.cabotenchants.sentient;

import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import dev.cabotmc.cabotenchants.sentient.packet.AbstractPacket;
import dev.cabotmc.cabotenchants.sentient.packet.WrapperPlayServerEntityMetadata;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class TargetManager {
  static HashMap<UUID, Set<LivingEntity>> targets = new HashMap<>();

  public static void addTarget(UUID uuid, LivingEntity entity) {
    if (!targets.containsKey(uuid)) {
      targets.put(uuid, new HashSet<>());
      targets.get(uuid).add(entity);
    }  else {
      if (!targets.get(uuid).add(entity)) {
        return;
      }
    }
    // send glowing packet
    sendPacket(Bukkit.getPlayer(uuid), createGlowPacket(entity, true));
  }

  public static Set<LivingEntity> getTargets(UUID uuid) {
    return targets.get(uuid);
  }

  public static void clearTargets(UUID uuid) {
    if (targets.containsKey(uuid)) {
      // send un glowing packets
      if (Bukkit.getPlayer(uuid) != null) {
        var diff = targets.get(uuid);
        diff.forEach(entity -> sendPacket(Bukkit.getPlayer(uuid), createGlowPacket(entity, false)));
      }
      targets.remove(uuid);

    }
  }

  static void sendPacket(Player p, ClientboundSetEntityDataPacket packet) {
    ((CraftPlayer) p).getHandle().connection.send(packet);
    //Bukkit.getLogger().info("Send packet to " + p.getName() + ": " + packet);
  }

  static public ClientboundSetEntityDataPacket createGlowPacket(Entity entity, boolean glow) {
    var e = ((CraftEntity) entity).getHandle();

    var record = new SynchedEntityData.DataValue(0, EntityDataSerializers.BYTE, (byte) (glow ? 0x40 : 0));
    return new ClientboundSetEntityDataPacket(entity.getEntityId(), List.of(record));
  }

}
