package azaka7.armorauras.main;

import java.awt.Color;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemHolder extends Item{
	
	public ItemHolder(){
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabs.TOOLS);
		
	}
	
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
		tooltip.add("\u00a77Activates auras added to worn armor");
		tooltip.add("\u00a78\u00a7nMax auras per armor type");
		int[] armorLimits = ArmorAuras.getArmorLimits();
		tooltip.add("\u00a78 Helmet: "+armorLimits[3]);
		tooltip.add("\u00a78 Chestplate: "+armorLimits[2]);
		tooltip.add("\u00a78 Leggings: "+armorLimits[1]);
		tooltip.add("\u00a78 Boots: "+armorLimits[0]);
    }
	
	@Override
	public void onUpdate(ItemStack thisStack, World world, Entity entityIn, int slot, boolean isSelected)
    {
		if(!(entityIn instanceof EntityLivingBase)){return;}
		EntityLivingBase entity = (EntityLivingBase) entityIn;
		
		if(isSelected || thisStack.equals(entity.getHeldItemOffhand())){
			for(ItemStack stack : entity.getArmorInventoryList()){
				if(stack == null || stack.getItem() == null){continue;}
				if(stack.getItem() instanceof ItemArmor && stack.hasTagCompound()){
					NBTTagCompound tags = stack.getTagCompound();
					NBTTagCompound auras = tags.getCompoundTag("a7armorauras");
					int[] effects = auras.getIntArray("auras");
					for(int i = 0; i < effects.length; i++){
						int ef = effects[i];
						PotionEffect pot = ItemAuraCharm.EnumAuraType.values()[ef].getEffect();
						byte radius = ArmorAuras.getAuraRadius();
						List<EntityLivingBase> near = world.getEntities(EntityLivingBase.class, EntitySelectors.withinRange(entity.posX, entity.posY+1, entity.posZ, radius));
						for(EntityLivingBase target : near){
							if(target != null && !target.equals(entity)){
								if(!target.isPotionActive(pot.getPotion())){
									target.addPotionEffect(new PotionEffect(pot));
								}
							}
						}
						
						Color color = new Color(pot.getPotion().getLiquidColor());
						if(pot.getPotion().equals(MobEffects.WITHER)){color = new Color(0xF2F0F2);}
						if(Math.random() < ArmorAuras.getParticleFactor() * (4.5D / effects.length)){
							double theta = 2*Math.PI;
							theta *= ((double) world.getWorldTime() % 500) / 500.0D;
							theta += 2*Math.PI*(double) ItemAuraCharm.EnumAuraType.values()[ef].getPos() / 25.0D;
							for(int j = 0; j < 5; j++){
								double t = theta + 2*Math.PI*j/5.0D;
								world.spawnParticle(EnumParticleTypes.SPELL_MOB, entity.posX + radius*Math.cos(t), entity.posY+0.2, entity.posZ + radius*Math.sin(t), color.getRed(), color.getBlue(), color.getGreen());
							}
						}
					}
					
					effects = auras.getIntArray("selfauras");
					for(int ef : effects){
						PotionEffect pot = ItemSelfAuraCharm.EnumSelfAuraType.values()[ef].getEffect();
						if(!entity.isPotionActive(pot.getPotion())){
							entity.addPotionEffect(pot);
						}
					}
					if(effects.length > 0){
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
			}
		}
		
		
    }
}
