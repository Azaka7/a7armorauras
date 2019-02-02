package azaka7.armorauras.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import azaka7.armorauras.main.ArmorAuras;
import azaka7.armorauras.main.ItemAuraCharm;
import azaka7.armorauras.main.ItemSelfAuraCharm;

@SideOnly(Side.CLIENT)
public class ClientHandler {
	
	private static ClientHandler INSTANCE = null;
	
	public static ClientHandler instance(){
		if(INSTANCE == null){
			INSTANCE = new ClientHandler();
			MinecraftForge.EVENT_BUS.register(INSTANCE);
		}
		return INSTANCE;
	}
	
	public void registerItemModels(HashMap<String, Item> items) {
		for(Map.Entry<String,Item> entry : items.entrySet()){
			Item item = entry.getValue();
			if(item.getHasSubtypes()){
				NonNullList<ItemStack> stacks = NonNullList.create();
				item.getSubItems(item.getCreativeTab(), stacks);
				for(ItemStack stack : stacks){
					ModelLoader.setCustomModelResourceLocation(stack.getItem(), stack.getMetadata(), new ModelResourceLocation(ArmorAuras.MODID+":"+entry.getKey()+stack.getMetadata(), "inventory"));
				}
			} else {
				ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(ArmorAuras.MODID+":"+entry.getKey(), "inventory"));
			}
		}
	}
	
	@SubscribeEvent
	public void tooltips(ItemTooltipEvent event){
		ItemStack stack = event.getItemStack();
		List<String> lines = event.getToolTip();
		
		if(stack.getItem() instanceof ItemArmor){
			if(stack == null || stack.getItem() == null){return;}
			if(stack.getItem() instanceof ItemArmor && stack.hasTagCompound()){
				NBTTagCompound tags = stack.getTagCompound();
				NBTTagCompound auras = tags.getCompoundTag("a7armorauras");
				int[] effects;
				if(auras.hasKey("auras")){
					effects = auras.getIntArray("auras");
					for(int i = 0; i < effects.length; i++){
						int ef = effects[i];
						ItemAuraCharm.EnumAuraType aura = ItemAuraCharm.EnumAuraType.values()[ef];
						lines.add(1, "\u00a76"+aura.getName()+" Aura");
					}
				}
				if(auras.hasKey("selfauras")){
					effects = auras.getIntArray("selfauras");
					for(int ef : effects){
						PotionEffect pot = ItemAuraCharm.EnumAuraType.values()[ef].getEffect();
						ItemSelfAuraCharm.EnumSelfAuraType aura = ItemSelfAuraCharm.EnumSelfAuraType.values()[ef];
						lines.add(1,"\u00a7b"+aura.getName()+" Inner Aura");
					}
				}
				
			}
		}
	}
	
}
