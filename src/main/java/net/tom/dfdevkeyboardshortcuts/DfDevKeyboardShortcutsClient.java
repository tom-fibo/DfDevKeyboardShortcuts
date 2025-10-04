package net.tom.dfdevkeyboardshortcuts;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class DfDevKeyboardShortcutsClient implements ClientModInitializer {

    private static boolean isExecuting = false;
    private static boolean updateInventory = false;
    public static final String NBT_TAG = "dfdevkeyboardshortcuts";
    private static Set<Block> noInteractBlocks = new HashSet<Block>(
            Arrays.asList(Blocks.OAK_WALL_SIGN, Blocks.CHEST, Blocks.IRON_BLOCK,
                    Blocks.OBSIDIAN, Blocks.PRISMARINE, Blocks.PURPUR_BLOCK, Blocks.NETHERRACK,
                    Blocks.RED_NETHER_BRICKS, Blocks.COAL_BLOCK, Blocks.END_STONE, Blocks.COBBLESTONE,
                    Blocks.OAK_PLANKS, Blocks.MOSSY_COBBLESTONE, Blocks.BRICKS, Blocks.LAPIS_ORE, Blocks.LAPIS_BLOCK,
                    Blocks.DIAMOND_BLOCK, Blocks.GOLD_BLOCK, Blocks.EMERALD_ORE, Blocks.EMERALD_BLOCK));
    private static Set<Block> shiftOffsetBlocks = new HashSet<Block>(
            Arrays.asList(Blocks.STONE, Blocks.PISTON, Blocks.STICKY_PISTON));

    public static boolean isActive(BlockPos targetPos) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (isExecuting || client.player == null || !client.player.isCreative() || client.currentScreen != null) {
            return false;
        }
        Block targetBlock = client.world.getBlockState(targetPos).getBlock();
        if (noInteractBlocks.contains(targetBlock)) {
            return false;
        }
        ItemStack mainHandStack = client.player.getMainHandStack();
        if (mainHandStack.getItem() != Items.NETHERITE_BLOCK) {
            return false;
        }
        NbtComponent customData = mainHandStack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData == null) {
            return false;
        }
        return customData.copyNbt().contains(NBT_TAG);
    }

    @Override
    public void onInitializeClient() {
        KeyBinds.register();

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (message.getString().contains("You are now in dev mode.")) {
                updateInventory = true;
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register((a) -> {
            if (updateInventory) {
                updateInventory = false;
                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;
                if (player != null && player.isCreative() && client.getNetworkHandler() != null) {

                    ItemStack netheriteBlock = new ItemStack(Items.NETHERITE_BLOCK);

                    NbtCompound nbt = new NbtCompound();

                    nbt.putBoolean(NBT_TAG, true);
                    netheriteBlock.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));

                    netheriteBlock.set(DataComponentTypes.CUSTOM_NAME,
                            Text.literal("Codeblock").styled(style -> style.withItalic(false)));

                    ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
                    networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36, netheriteBlock));
                    networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(43, new ItemStack(Items.AIR)));
                    networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(44, new ItemStack(Items.AIR)));
                    player.getInventory().setStack(0, netheriteBlock);
                    player.getInventory().setStack(7, new ItemStack(Items.AIR));
                    player.getInventory().setStack(8, new ItemStack(Items.AIR));
                }
            }
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!isActive(hitResult.getBlockPos()))
                return ActionResult.PASS;
            if (world.isClient) {
                placeBlock(player, Items.IRON_BLOCK, Items.OBSIDIAN, hitResult);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (!isActive(pos))
                return ActionResult.PASS;
            if (world.isClient) {
                BlockHitResult hitResult = new BlockHitResult(pos.toCenterPos(), direction, pos, false);
                placeBlock(player, Items.PRISMARINE, Items.PURPUR_BLOCK, hitResult);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });
    }

    public static void placeBlock(PlayerEntity player, Item normalItem, Item sneakItem, BlockHitResult hitResult) {
        if (isExecuting)
            return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.interactionManager == null || client.getNetworkHandler() == null) {
            return;
        }

        isExecuting = true;
        try {

            // Place the new block
            int slot = 36 + player.getInventory().getSlotWithStack(player.getMainHandStack()); // First hotbar slot

            ItemStack originalStack = player.getInventory().getStack(0).copy();
            Item itemToPlace = player.isSneaking() ? sneakItem : normalItem;
            ItemStack newStack = new ItemStack(itemToPlace);

            //BlockPos signPos = hitResult.getBlockPos().offset(Direction.WEST);

            client.getNetworkHandler().sendPacket(new CreativeInventoryActionC2SPacket(slot, newStack));

            client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, hitResult);

            if (client.player.isSneaking()
                    && shiftOffsetBlocks.contains(client.world.getBlockState(hitResult.getBlockPos()).getBlock())) {
                BlockPos stoneOffsetPos = hitResult.getBlockPos().offset(Direction.SOUTH);
                client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND,
                        new BlockHitResult(stoneOffsetPos.toCenterPos(), Direction.UP, stoneOffsetPos, false));
                //signPos = stoneOffsetPos.offset(Direction.WEST);
            }

            //client.interactionManager.interactBlock(client.player, Hand.OFF_HAND,
            //        new BlockHitResult(signPos.toCenterPos(), Direction.UP, signPos, false));

            client.getNetworkHandler().sendPacket(new CreativeInventoryActionC2SPacket(slot, originalStack));
        } finally {
            isExecuting = false;
        }
    }
}
