package azaka7.armorauras.main;

import java.awt.Color;
import java.util.List;

import javax.annotation.Nullable;

import azaka7.armorauras.client.ClientHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSelfAuraCharm extends Item{
	
	public ItemSelfAuraCharm(){
		this.setMaxDamage(0);
	    this.setHasSubtypes(true);
	    this.setMaxStackSize(1);
	    this.setCreativeTab(CreativeTabs.TOOLS);
	}
	
	@Override
	public int getMetadata(int damage) {
	  return damage;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
    {
		return this.getUnlocalizedName()+"."+getAuraType(stack).toString().toLowerCase();
    }
	
	@Override
	public int getMetadata(ItemStack stack)
    {
        return stack.getItemDamage() >= EnumSelfAuraType.values().length ? 0 : stack.getItemDamage();
    }
	
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
		ClientHandler client = ClientHandler.instance();
		if(stack.getItem() == this){
			tooltip.add("\u00a76"+EnumSelfAuraType.values()[stack.getMetadata()].getName()+" "+client.localize("a7armorauras.tooltip.inneraura"));
			tooltip.add("\u00a77 "+client.localize("a7armorauras.tooltip.charm.activate"));
			tooltip.add("\u00a77 "+client.localize("a7armorauras.tooltip.charm.armor"));
		}
    }
	
	@SideOnly(Side.CLIENT)
	  @Override
	  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
	  {
		if(this.isInCreativeTab(tab)){
			for (EnumSelfAuraType aura : EnumSelfAuraType.values()) {
			      ItemStack subItemStack = new ItemStack(this, 1, aura.getMetadata());
			      subItems.add(subItemStack);
			    }
		}
	  }
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entityIn, int slot, boolean isSelected)
    {
		if(!(entityIn instanceof EntityLivingBase)){return;}
		EntityLivingBase entity = (EntityLivingBase) entityIn;
		
		if(isSelected || stack.equals(entity.getHeldItemOffhand())){
			PotionEffect pot = ItemSelfAuraCharm.EnumSelfAuraType.values()[stack.getMetadata()].getEffect();
			if(pot == null){return;}
			if(!entity.isPotionActive(pot.getPotion())){
				entity.addPotionEffect(pot);
			}
			if(world.getWorldTime() % 5 == 0){
				double theta = 2*Math.PI;
				theta *= ((double) world.getWorldTime() % 360) / 360.0D;
				theta = 2*Math.PI - theta;
				for(int j = 0; j < 5; j++){
					double t = theta + 2*Math.PI*j/5.0D;
					world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, entity.posX + 1*Math.cos(t), entity.posY+0.2, entity.posZ + 1*Math.sin(t), 0,0,0);
				}
			}
		}
    }
	
	public EnumSelfAuraType getAuraType(ItemStack stack){
		if(stack == null || stack.getItem() != this 
				|| stack.getMetadata() >= EnumSelfAuraType.values().length){return EnumSelfAuraType.NULL;}
		
		return EnumSelfAuraType.values()[stack.getMetadata()];
	}
	
	public static enum EnumSelfAuraType implements IStringSerializable{
		NULL(0,0,"null", null),
		SPEED(1,0,"speed", new PotionEffect(MobEffects.SPEED, 60, 0, false, false)),
		SPEED2(2,0,"speed2", new PotionEffect(MobEffects.SPEED, 40, 1, false, false)),
		SPEED3(3,0,"speed3", new PotionEffect(MobEffects.SPEED, 20, 2, false, false)),
		STRONG(4,1,"strength", new PotionEffect(MobEffects.STRENGTH, 60, 0, false, false)),
		STRONG2(5,1,"strength2", new PotionEffect(MobEffects.STRENGTH, 30, 1, false, false)),
		RESIST(6,2,"resistance", new PotionEffect(MobEffects.RESISTANCE, 60, 2, false, false)),
		RESIST1(7,2,"resistance2", new PotionEffect(MobEffects.RESISTANCE, 40, 0, false, false)),
		RESIST2(8,2,"resistance3", new PotionEffect(MobEffects.RESISTANCE, 20, 1, false, false)),
		HEAL(9,3,"healing", new PotionEffect(MobEffects.REGENERATION, 120, 0, false, false)),
		HEAL2(10,3,"healing2", new PotionEffect(MobEffects.REGENERATION, 60, 1, false, false)),
		HASTE(11,4,"haste", new PotionEffect(MobEffects.HASTE, 20, 0, false, false)),
		LUCK(12,5,"luck", new PotionEffect(MobEffects.LUCK, 200, 0, false, false));
		
    	//PotionEffect effect = new PotionEffect(potion, duration, amplifier, isFromBeacon, canSeeParticles);
		
		private final int meta;
		private final int pos;
	    private final String name;
	    private final PotionEffect effect;
	    
	    private EnumSelfAuraType(int meta, int pos, String name, PotionEffect effect){
	    	this.meta = meta;
	    	this.pos = pos;
	    	this.name = name;
	    	this.effect = effect;
	    }

		public int getMetadata() {
			return meta;
		}

		public String getUnlocalizedName() {
			return name;
		}
		
		public String getName(){
			return ClientHandler.instance().localize("a7armorauras.auras."+name);
		}
		
		public PotionEffect getEffect(){
			if(this.effect == null){return null;}
			return new PotionEffect(this.effect);
		}

		public double getPos() {
			return pos;
		}
		
	}
	
}
