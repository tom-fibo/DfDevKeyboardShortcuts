package net.tom.dfdevkeyboardshortcuts;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class DfDevKeyboardShortcutsClient implements ClientModInitializer {

    private static boolean isExecuting = false;
    public static boolean updateInventory = false;
    public static final String NBT_TAG = "dfdevkeyboardshortcuts";
    private static Set<Block> noInteractBlocks = new HashSet<Block>(
            Arrays.asList(Blocks.OAK_WALL_SIGN, Blocks.CHEST, Blocks.IRON_BLOCK,
                    Blocks.OBSIDIAN, Blocks.PRISMARINE, Blocks.PURPUR_BLOCK, Blocks.NETHERRACK,
                    Blocks.RED_NETHER_BRICKS, Blocks.COAL_BLOCK, Blocks.END_STONE, Blocks.COBBLESTONE,
                    Blocks.OAK_PLANKS, Blocks.MOSSY_COBBLESTONE, Blocks.BRICKS, Blocks.LAPIS_ORE, Blocks.LAPIS_BLOCK,
                    Blocks.DIAMOND_BLOCK, Blocks.GOLD_BLOCK, Blocks.EMERALD_ORE, Blocks.EMERALD_BLOCK,
                    Blocks.NETHERITE_BLOCK));

    public static boolean isActive(BlockPos targetPos) {
        Minecraft client = Minecraft.getInstance();
        if (isExecuting || client.player == null || !client.player.isCreative() || client.gui.screen() != null) {
            return false;
        }
        Block targetBlock = client.level.getBlockState(targetPos).getBlock();
        if (noInteractBlocks.contains(targetBlock)) {
            return false;
        }
        ItemStack mainHandStack = client.player.getMainHandItem();
        if (mainHandStack.getItem() != Items.CONCRETE.lime()) {
            return false;
        }
        CustomData customData = mainHandStack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return false;
        }
        return customData.copyTag().contains(NBT_TAG);
    }

    public static ItemStack getCodeBlock() {
        ItemStack codeblock = new ItemStack(Items.CONCRETE.lime());

        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean(NBT_TAG, true);
        codeblock.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));

        codeblock.set(DataComponents.CUSTOM_NAME,
                Component.literal("Codeblock").withStyle(style -> style.withItalic(false)));

        return codeblock;
    }

    public static ItemStack getReferenceBook() {
        ItemStack referenceBook = new ItemStack(Items.WRITTEN_BOOK);

        CompoundTag hypercubeItemInstance = new CompoundTag();
        hypercubeItemInstance.putString("hypercube:item_instance", "reference_book");

        CompoundTag publicBukkitValues = new CompoundTag();
        publicBukkitValues.put("PublicBukkitValues", hypercubeItemInstance);
        referenceBook.set(DataComponents.CUSTOM_DATA, CustomData.of(publicBukkitValues));

        referenceBook.set(DataComponents.CUSTOM_NAME,
                Component.literal("Reference Book").withStyle(style -> style.withItalic(false)));

        return referenceBook;
    }

    @Override
    public void onInitializeClient() {
        //KeyBinds.register();

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (message.getString().contains("You are now in dev mode.")) {
                updateInventory = true;
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register((a) -> {
            if (updateInventory) {
                updateInventory = false;
                Minecraft client = Minecraft.getInstance();
                LocalPlayer player = client.player;
                if (player != null && player.isCreative() && client.getConnection() != null) {

                    ItemStack codeblock = getCodeBlock();
                    ItemStack referenceBook = getReferenceBook();

                    ClientPacketListener networkHandler = client.getConnection();
                    Inventory inventory = player.getInventory();
                    networkHandler.send(new ServerboundSetCreativeModeSlotPacket(36, codeblock));
                    inventory.setItem(0, codeblock);
                    networkHandler.send(new ServerboundSetCreativeModeSlotPacket(17, referenceBook));
                    inventory.setItem(17, referenceBook);
                    if (inventory.getItem(7).getItem() == Items.DIAMOND
                            && inventory.getItem(7).getHoverName().getString().contains("Code Blocks")) {
                        networkHandler.send(new ServerboundSetCreativeModeSlotPacket(43, new ItemStack(Items.AIR)));
                        inventory.setItem(7, new ItemStack(Items.AIR));
                    }
                    if (inventory.getItem(8).getItem() == Items.IRON_INGOT
                            && inventory.getItem(8).getHoverName().getString().contains("Values")) {
                        networkHandler.send(new ServerboundSetCreativeModeSlotPacket(44, new ItemStack(Items.AIR)));
                        inventory.setItem(8, new ItemStack(Items.AIR));
                    }
                }
            }
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!isActive(hitResult.getBlockPos()))
                return InteractionResult.PASS;
            if (world.isClientSide()) {
                placeBlock(player, Items.IRON_BLOCK, Items.OBSIDIAN, hitResult);
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (!isActive(pos))
                return InteractionResult.PASS;
            if (world.isClientSide()) {
                BlockHitResult hitResult = new BlockHitResult(Vec3.atCenterOf(pos), direction, pos, false);
                placeBlock(player, Items.PRISMARINE, Items.PURPUR_BLOCK, hitResult);
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

    }

    public static void placeBlock(Player player, Item normalItem, Item sneakItem, BlockHitResult hitResult) {
        if (isExecuting)
            return;

        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.gameMode == null || client.getConnection() == null) {
            return;
        }
        isExecuting = true;
        try {

            int slot = player.getInventory().findSlotMatchingItem(player.getMainHandItem()); // First hotbar slot
            boolean isSneaking = client.player.isShiftKeyDown();

            //ItemStack originalStack = player.getInventory().getItem(slot).copy();
            Item itemToPlace = isSneaking ? sneakItem : normalItem;
            DfDevKeyboardShortcuts.LOGGER.info("Placing {}", itemToPlace);
            ItemStack newStack = new ItemStack(itemToPlace);

            //BlockPos signPos = hitResult.getBlockPos().offset(Direction.WEST);

            client.getConnection().send(new ServerboundSetCreativeModeSlotPacket(slot + 36, newStack));

            if (isSneaking) {
                client.getConnection().send(
                        new ServerboundPlayerInputPacket(new Input(false, false, false, false, false, false, false)));
            }
            client.gameMode.useItemOn(client.player, InteractionHand.MAIN_HAND, hitResult);
            if (isSneaking) {
                client.getConnection().send(new ServerboundPlayerInputPacket(client.player.getLastSentInput()));
            }

            //client.gameMode.useItemOn(client.player, InteractionHand.OFF_HAND,
            //        new BlockHitResult(signPos.toCenterPos(), Direction.UP, signPos, false));

            client.getConnection().send(new ServerboundSetCreativeModeSlotPacket(slot + 36, getCodeBlock()));
            player.getInventory().setItem(slot, getCodeBlock());
        } finally {
            isExecuting = false;
        }
    }
}
