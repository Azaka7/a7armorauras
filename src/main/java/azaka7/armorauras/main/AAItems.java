package azaka7.armorauras.main;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import azaka7.armorauras.client.ClientHandler;

public class AAItems {
	
	private static final AAItems INSTANCE = new AAItems();
	private static final HashMap<String,Item> items = new HashMap<String,Item>();
	private static final boolean isClient = ArmorAuras.isClient();

    private static Item auraCharm = (new ItemAuraCharm()).setUnlocalizedName("aura_charm").setRegistryName("aura_charm");
	private static Item selfAuraCharm = (new ItemSelfAuraCharm()).setUnlocalizedName("self_aura_charm").setRegistryName("self_charm");
    private static Item holder = (new ItemHolder()).setUnlocalizedName("holder").setRegistryName("holder");
	
	public static void registerModItems(){
		MinecraftForge.EVENT_BUS.register(INSTANCE);
	}
	
	@SubscribeEvent
	public void registerItems(final RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> reg = event.getRegistry();
		items.clear();
		
   		register("aura_charm", auraCharm);
   		register("self_charm", selfAuraCharm);
   		register("holder", holder);
   		
   		if(isClient){
   			ClientHandler.instance().registerItemModels(items);
   		}
	}
	
	@SubscribeEvent
	public void anvilEvent(final AnvilUpdateEvent event) {
		if(event.getOutput() != null && event.getOutput().getCount() > 0){return;}
		final ItemStack left = event.getLeft();
		final ItemStack right = event.getRight();
		if(left != null && left.getItem() != null & left.getItem() instanceof ItemArmor){
			if(!right.isEmpty() && right.getItem() instanceof ItemAuraCharm){
				//Retrieve item stack and NBT data
				if(((ItemAuraCharm) right.getItem()).getAuraType(right).getMetadata() == 0){
					return;
				}
				
				ItemStack ret = left.copy();
				
				NBTTagCompound tags = ret.getTagCompound();
				if(tags == null){tags = new NBTTagCompound();}
				if(!tags.hasKey("a7armorauras")){
					tags.setTag("a7armorauras", new NBTTagCompound());
				}
				
				NBTTagCompound auras = tags.getCompoundTag("a7armorauras");
				ArrayList<ItemAuraCharm.EnumAuraType> types = new ArrayList<ItemAuraCharm.EnumAuraType>();
				if(auras.hasKey("auras")){
					for(int i : auras.getIntArray("auras")){
						types.add(ItemAuraCharm.EnumAuraType.values()[i]);
					}
				}
				
				//If the armor piece has max aura count, cancel
				final int armorSlot = ((ItemArmor) left.getItem()).getEquipmentSlot().getIndex();
				if(types.size() >= ArmorAuras.getArmorLimits()[armorSlot]){ return; }
				
				//If the armor already has an aura with a similar effect, cancel
				ItemAuraCharm.EnumAuraType newType = ((ItemAuraCharm) right.getItem()).getAuraType(right);
				for(ItemAuraCharm.EnumAuraType type : types){
					if(type.getPos() == newType.getPos()){
						return;
					}
				}
				
				//Add the aura to the armor
				types.add(newType);
				
				int[] ef = new int[types.size()];
				for(int i = 0; i < ef.length; i++){
					ef[i] = types.get(i).getMetadata();
				}
				
				auras.setIntArray("auras", ef);
				tags.setTag("a7armorauras", auras);
				ret.setTagCompound(tags);
				
				event.setOutput(ret);
				event.setCost(ArmorAuras.getXPCost());
			}
			if(!right.isEmpty() && right.getItem() instanceof ItemSelfAuraCharm){
				//Retrieve item stack and NBT data
				if(((ItemSelfAuraCharm) right.getItem()).getAuraType(right).getMetadata() == 0){
					return;
				}
				
				ItemStack ret = left.copy();
				
				NBTTagCompound tags = ret.getTagCompound();
				if(tags == null){tags = new NBTTagCompound();}
				if(!tags.hasKey("a7armorauras")){
					tags.setTag("a7armorauras", new NBTTagCompound());
				}
				
				NBTTagCompound auras = tags.getCompoundTag("a7armorauras");
				ArrayList<ItemSelfAuraCharm.EnumSelfAuraType> types = new ArrayList<ItemSelfAuraCharm.EnumSelfAuraType>();
				if(auras.hasKey("selfauras")){
					for(int i : auras.getIntArray("selfauras")){
						types.add(ItemSelfAuraCharm.EnumSelfAuraType.values()[i]);
					}
				}
				
				//If the armor piece has max aura count, cancel
				final int armorSlot = ((ItemArmor) left.getItem()).getEquipmentSlot().getIndex();
				if(types.size() >= ArmorAuras.getArmorLimits()[armorSlot]){ return; }
				
				//If the armor already has an aura with a similar effect, cancel
				ItemSelfAuraCharm.EnumSelfAuraType newType = ((ItemSelfAuraCharm) right.getItem()).getAuraType(right);
				for(ItemSelfAuraCharm.EnumSelfAuraType type : types){
					if(type.getPos() == newType.getPos()){
						return;
					}
				}
				
				//Add the aura to the armor
				types.add(newType);
				
				int[] ef = new int[types.size()];
				for(int i = 0; i < ef.length; i++){
					ef[i] = types.get(i).getMetadata();
				}
				
				auras.setIntArray("selfauras", ef);
				tags.setTag("a7armorauras", auras);
				ret.setTagCompound(tags);
				
				event.setOutput(ret);
				event.setCost(30);
			}
		}
	}
	
	private static void register(String id, Item item){
		items.put(id, item);
        GameData.register_impl(item);
	}
	
	public static Item getItem(String id){
		return items.get(id);
	}
	
}
