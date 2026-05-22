package eu.macsworks.projectnhm.games.nhmGames.utils;

import lombok.experimental.UtilityClass;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.Tag;
import org.bukkit.Bukkit;
import org.bukkit.util.Vector;
import org.bukkit.block.data.BlockData;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class SchematicLoader {

    public Map<Vector, BlockData> readSchematic(File schematic) throws IOException {
        CompoundTag root = (CompoundTag) NBTUtil.read(schematic).getTag();

        int width  = root.getShort("Width");
        int height = root.getShort("Height");
        int length = root.getShort("Length");

        int[] rawOffset = root.containsKey("Offset") ? root.getIntArray("Offset") : new int[]{0, 0, 0};
        int offsetY = rawOffset[1];

        // Build palette: palette index -> BlockData
        CompoundTag paletteTag = root.getCompoundTag("Palette");
        BlockData[] palette = new BlockData[paletteTag.size()];
        for (Map.Entry<String, Tag<?>> entry : paletteTag) {
            int idx = ((IntTag) entry.getValue()).asInt();
            palette[idx] = Bukkit.createBlockData(entry.getKey());
        }

        // Decode varint-encoded block indices (Sponge schematic format)
        byte[] raw = root.getByteArray("BlockData");
        int[] blocks = new int[width * height * length];
        int i = 0, pos = 0;
        while (pos < raw.length) {
            int value = 0, shift = 0;
            byte b;
            do {
                b = raw[pos++];
                value |= (b & 0x7F) << shift;
                shift += 7;
            } while ((b & 0x80) != 0);
            blocks[i++] = value;
        }

        // Layout: index = (y * length + z) * width + x
        Map<Vector, BlockData> result = new HashMap<>(width * height * length);
        for (int y = 0; y < height; y++) {
            for (int z = 0; z < length; z++) {
                for (int x = 0; x < width; x++) {
                    BlockData data = palette[blocks[(y * length + z) * width + x]];
                    result.put(new Vector(x, y + offsetY, z), data);
                }
            }
        }

        return result;
    }
}