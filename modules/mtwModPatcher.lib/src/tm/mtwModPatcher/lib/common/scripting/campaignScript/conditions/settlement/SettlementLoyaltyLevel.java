package tm.mtwModPatcher.lib.common.scripting.campaignScript.conditions.settlement;

import tm.common.Ctm;
import tm.mtwModPatcher.lib.common.scripting.campaignScript.core.Condition;

/** Trigger Requirements: settlement
 *  How loyal is the settlement?
 */
public class SettlementLoyaltyLevel extends Condition {

	private String operator;
	private String loyaltyLevel;

	public static final String Happy1 = "loyalty_happy";
	public static final String Content2 = "loyalty_content";
	public static final String Disillusioned3 = "loyalty_disillusioned";
	public static final String Rioting4 = "loyalty_rioting";
	public static final String Revolting5 = "loyalty_revolting";

	@Override
	public String getString() {
		return Ctm.msgFormat("SettlementLoyaltyLevel {0} {1}", operator, loyaltyLevel);
	}

	public SettlementLoyaltyLevel(String operator , String loyaltyLevel) {
		this.loyaltyLevel = loyaltyLevel;
		this.operator = operator;
	}
}
