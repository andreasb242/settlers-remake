package jsettlers.logic.newmovable.strategies.soldiers;

import jsettlers.common.buildings.OccupyerPlace.ESoldierType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.logic.newmovable.NewMovable;
import jsettlers.logic.newmovable.interfaces.IAttackable;

/**
 * Strategy for swordsman and pikeman {@link NewMovable}s.
 * 
 * @author Andreas Eberle
 * 
 */
public final class InfantryStrategy extends SoldierStrategy {
	private static final long serialVersionUID = -2367165698305111060L;
	private static final float INFANTRY_ATTACK_DURATION = 1;

	public InfantryStrategy(NewMovable movable, EMovableType movableType) {
		super(movable, movableType);
	}

	@Override
	public ESoldierType getSoldierType() {
		return ESoldierType.INFANTRY;
	}

	@Override
	protected boolean isEnemyAttackable(IAttackable enemy) {
		return EDirection.getDirection(super.getPos(), enemy.getPos()) != null;
	}

	@Override
	protected void startAttackAnimation(IAttackable enemy) {
		super.playAction(EAction.ACTION1, INFANTRY_ATTACK_DURATION);
	}

	@Override
	protected void hitEnemy(IAttackable enemy) {
		enemy.hit(0.1f); // decrease the enemy's health
	}

}
