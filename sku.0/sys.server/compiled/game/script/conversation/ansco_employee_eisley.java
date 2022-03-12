package script.conversation;

import script.library.*;
import script.*;

import java.util.ArrayList;

public class ansco_employee_eisley extends script.base_script
{  
	private static final String C_STRING_FILE = "conversation/ansco_employee_eisley";
    private static final String CONVERSATION_NAME = "ansco_employee_eisley";
	private static final String CONVERSATION_SCRIPT = "conversation.ansco_employee_eisley";
	private static final String QUEST_NAME = "ansco_recover_prototype";
	private static final String BRANCH_ID = "conversation.ansco_employee_eisley.branchId";
	private static final String QUEST_REWARD_ITEM = "object/tangible/loot/quest/adv_shellfish_harvester_schematic.iff";
	private static final String QUEST_COLLECT_REWARD_TASK = "ansc_collect_reward";
	private static final String QUEST_COLLECT_REWARD_SIGNAL = "collected_reward";
	
	//Branch IDs
    private static final int C_BRANCH_NPC_OPENING_COMPLIANT =  100;	
	private static final int C_BRANCH_PLAYER_ASK_PROBLEM =  200;
	private static final int C_BRANCH_PLAYER_REJECT_PROBLEM =  300;	
	private static final int C_BRANCH_PLAYER_GM_MENU = 400;
	private static final int C_BRANCH_PLAYER_QUEST_INCOMPLETE = 500;
	private static final int C_BRANCH_PLAYER_COMPLETE_QUEST = 600;
	private static final int C_BRANCH_PLAYER_QUEST_COMPLETED = 700;
	
			
	
	
	//NPC Responses
	private static final String NPC_RESPONSE_DONT_BOTHER = "s_npc_initial_greeting_dont_bother";
	private static final String NPC_RESPONSE_PROBLEM = "s_npc_tell_problem";
	private static final String NPC_RESPONSE_ACCEPT_HELP = "s_npc_accept_help";
	private static final String NPC_RESPONSE_GET_LOST = "s_npc_get_lost";
	private static final String NPC_RESPONSE_GM_MENU = "s_npc_gm_menu";
	private static final String NPC_RESPONSE_GM_QUEST_CLEARED = "s_npc_gm_quest_cleared";
	private static final String NPC_RESPONSE_GM_QUEST_REWARD = "s_npc_gm_quest_reward";
	private static final String NPC_RESPONSE_DO_QUEST = "s_npc_do_quest";
	private static final String NPC_RESPONSE_THANK_HELP = "s_npc_thank_help";
	private static final String NPC_RESPONSE_RECOVER_QUESTION = "s_npc_recover_question";
	private static final String NPC_RESPONSE_RECOVER_BROKEN = "s_npc_recover_broken";
	
	
	//Player Resposes	
	private static final String PLAYER_GM_TEST_MENU =  "s_gm_test_menu";
	private static final String PLAYER_RESPONSE_ASK_PROBLEM = "s_player_ask_problem";
	private static final String PLAYER_RESPONSE_REFUSE_NO_TIME = "s_player_no_time";
	private static final String PLAYER_RESPONSE_OFFER_HELP = "s_player_offer_help";
	private static final String PLAYER_GM_CLEAR_QUEST = "s_player_clear_quest";
	private static final String PLAYER_GM_GRANT_REWARD = "s_player_grant_reward";	
	private static final String PLAYER_RESPONSE_HAVE_PROTOTYPE = "s_player_has_prototype";	

	
	
    public ansco_employee_eisley()
    {
    }
    public static String c_stringFile = "conversation/ansco_employee_eisley";
    
    public boolean ansco_employee_eisley_condition__defaultCondition(obj_id player, obj_id npc) throws InterruptedException
    {
        return true;
    }
    
    
    public boolean ansco_employee_eisley_condition_condition_isGm(obj_id player, obj_id npc) throws InterruptedException
    {
        return (hasObjVar(player, "gm"));
    }
    
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        if ((!isMob(self)) || (isPlayer(self)))
        {
            detachScript(self, CONVERSATION_SCRIPT);
        }
        setCondition(self, CONDITION_CONVERSABLE);
        setCondition(self, CONDITION_INTERESTING);
        setInvulnerable(self, true);
        return SCRIPT_CONTINUE;
    }
    
    public int OnAttach(obj_id self) throws InterruptedException
    {
        setCondition(self, CONDITION_CONVERSABLE);
        setCondition(self, CONDITION_INTERESTING);
        setInvulnerable(self, true);
        return SCRIPT_CONTINUE;
    }
    
    public int OnObjectMenuRequest(obj_id self, obj_id player, menu_info menuInfo) throws InterruptedException
    {
        int menu = menuInfo.addRootMenu(menu_info_types.CONVERSE_START, null);
        menu_info_data menuInfoData = menuInfo.getMenuItemById(menu);
        menuInfoData.setServerNotify(false);
        setCondition(self, CONDITION_CONVERSABLE);
        setCondition(self, CONDITION_INTERESTING);
        faceTo(self, player);
        return SCRIPT_CONTINUE;
    }
    
    public int OnIncapacitated(obj_id self, obj_id killer) throws InterruptedException
    {
        clearCondition(self, CONDITION_CONVERSABLE);
        clearCondition(self, CONDITION_INTERESTING);
        detachScript(self, CONVERSATION_SCRIPT);
        return SCRIPT_CONTINUE;
    }
    
    
    public int OnStartNpcConversation(obj_id self, obj_id player) throws InterruptedException
    {
		
        obj_id npc = self;		
		
        if (ai_lib.isInCombat(npc) || ai_lib.isInCombat(player))
        {
            return SCRIPT_OVERRIDE;
        }
      
		
		utils.removeScriptVar(player, BRANCH_ID);		 
		
		if (ansco_employee_eisley_condition_activeQuest(player, npc))
		{
		
			if (groundquests.isTaskActive(player, QUEST_NAME, QUEST_COLLECT_REWARD_TASK))
			{			
				return ansco_employee_eisley_NPC_SetupCompleteQuest(player, npc);
			}
			else
			{
			
				string_id message = new string_id(C_STRING_FILE, NPC_RESPONSE_DO_QUEST);

				if (ansco_employee_eisley_condition_condition_isGm(player, npc))
				{				
					utils.setScriptVar(player, BRANCH_ID, C_BRANCH_PLAYER_QUEST_INCOMPLETE);		 
					ArrayList<string_id> responses = new ArrayList<string_id>();
					responses.add(new string_id(C_STRING_FILE, PLAYER_GM_TEST_MENU));				
					npcStartConversation(player, npc, CONVERSATION_NAME, message, responses.toArray(new string_id[0]));		
					
		
				} 		
				else
				{

					npcSpeak(player, message);
				}	

				return SCRIPT_CONTINUE;			
			}
		
		
		}


        if (ansco_employee_eisley_condition_completedQuest(player, npc))
        {		
		
			string_id message = new string_id(C_STRING_FILE, NPC_RESPONSE_THANK_HELP);
		
			if (ansco_employee_eisley_condition_condition_isGm(player, npc))
			{   		
				utils.setScriptVar(player, BRANCH_ID, C_BRANCH_PLAYER_QUEST_COMPLETED);			
				ArrayList<string_id> responses = new ArrayList<string_id>();
				responses.add(new string_id(C_STRING_FILE, PLAYER_GM_TEST_MENU));				
				npcStartConversation(player, npc, CONVERSATION_NAME, message, responses.toArray(new string_id[0]));				
			} 		
			else
			{				
				npcSpeak(player, message);				
			}            
			
			return SCRIPT_CONTINUE;			
        }
        
		//if we reached this point offer the quest
		

		if (ansco_employee_eisley_NPC_StartQuestOffer(player, npc) == SCRIPT_CONTINUE)
		{
			return SCRIPT_CONTINUE;
		}
		

        return SCRIPT_CONTINUE;
    }
    
    public int OnNpcConversationResponse(obj_id self, String conversationId, obj_id player, string_id response) throws InterruptedException
    {
        if (!conversationId.equals(CONVERSATION_NAME))
        {
            return SCRIPT_CONTINUE;
        }       

         obj_id npc = self;
         int branchId = utils.getIntScriptVar(player, BRANCH_ID);
		 
         
         if (branchId == C_BRANCH_NPC_OPENING_COMPLIANT 
		 	&& ansco_employee_eisley_branch_opening_complaint_handler(player, npc, response) == SCRIPT_CONTINUE)
         {
         	return SCRIPT_CONTINUE;			
         }


         if (branchId == C_BRANCH_PLAYER_ASK_PROBLEM 
		 	&& ansco_employee_eisley_branch_ask_problem_handler(player, npc, response) == SCRIPT_CONTINUE)
         {
         	return SCRIPT_CONTINUE;			
         }


		if (branchId == C_BRANCH_PLAYER_GM_MENU
			&& ansco_employee_eisley_branch_gm_menu(player, npc, response) == SCRIPT_CONTINUE
		)
		{
			return SCRIPT_CONTINUE;		
		}

		if (branchId == C_BRANCH_PLAYER_COMPLETE_QUEST
				&& ansco_employee_eisley_NPC_CompleteQuest(player, npc, response) == SCRIPT_CONTINUE)		
		{
			return SCRIPT_CONTINUE;		
		}
		
		if (branchId == C_BRANCH_PLAYER_QUEST_INCOMPLETE
			&& ansco_employee_eisley_NPC_CompleteQuest(player, npc, response) == SCRIPT_CONTINUE)
		{
			return SCRIPT_CONTINUE;		
		}
		
		
		if (branchId == C_BRANCH_PLAYER_QUEST_COMPLETED
			&& ansco_employee_eisley_NPC_QuestCompleted(player, npc, response) == SCRIPT_CONTINUE)
		{
			return SCRIPT_CONTINUE;		
		}


        
        return SCRIPT_CONTINUE;
    }
    
    //Show the conversation options
    
   public boolean startConversationWithNPC(obj_id player, obj_id npc, String convoName,
        string_id greetingId, prose_package greetingProse, string_id[] responses) throws InterruptedException
    {
        Object[] objects = new Object[responses.length];
        System.arraycopy(responses, 0, objects, 0, responses.length);
        return npcStartConversation(player, npc, convoName, greetingId, greetingProse, objects);
    }    
    
    //Offer Quest Methods
    

    private int ansco_employee_eisley_NPC_StartQuestOffer(obj_id player, obj_id npc) throws InterruptedException
    {
    
     	 
        ArrayList<string_id> responses = new ArrayList<string_id>();

        
        if (ansco_employee_eisley_condition_condition_isGm(player, npc))
        {            
            responses.add(new string_id(C_STRING_FILE, PLAYER_GM_TEST_MENU));
        } 		

        
		string_id message = new string_id(C_STRING_FILE, NPC_RESPONSE_DONT_BOTHER);
		responses.add(new string_id(C_STRING_FILE, PLAYER_RESPONSE_ASK_PROBLEM));
		responses.add(new string_id(C_STRING_FILE, PLAYER_RESPONSE_REFUSE_NO_TIME));
        utils.setScriptVar(player, BRANCH_ID, C_BRANCH_NPC_OPENING_COMPLIANT);		 
		
        npcStartConversation(player, npc, CONVERSATION_NAME, message, responses.toArray(new string_id[0]));

    
        return SCRIPT_CONTINUE;
    }
	
	private int ansco_employee_eisley_NPC_SetupCompleteQuest(obj_id player, obj_id npc) throws InterruptedException
	{
			 
        ArrayList<string_id> responses = new ArrayList<string_id>();

        
        if (ansco_employee_eisley_condition_condition_isGm(player, npc))
        {            
            responses.add(new string_id(C_STRING_FILE, PLAYER_GM_TEST_MENU));
        } 		

        
		string_id message = new string_id(C_STRING_FILE, NPC_RESPONSE_RECOVER_QUESTION);
		responses.add(new string_id(C_STRING_FILE, PLAYER_RESPONSE_HAVE_PROTOTYPE));	
        utils.setScriptVar(player, BRANCH_ID, C_BRANCH_PLAYER_COMPLETE_QUEST);	
		npcStartConversation(player, npc, CONVERSATION_NAME, message, responses.toArray(new string_id[0]));				
		return SCRIPT_CONTINUE;
	}
	
	private int ansco_employee_eisley_NPC_CompleteQuest(obj_id player, obj_id npc, string_id response) throws InterruptedException
	{	
	
		 ArrayList<string_id> responses = new ArrayList<string_id>();
	
	
		if (response.equals(PLAYER_GM_TEST_MENU))
		{
			utils.setScriptVar(player, BRANCH_ID, C_BRANCH_PLAYER_GM_MENU);
			string_id message = new string_id(C_STRING_FILE, NPC_RESPONSE_GM_MENU);
			npcSpeak(player, message);			
			responses.add(new string_id(C_STRING_FILE, PLAYER_GM_CLEAR_QUEST));
			responses.add(new string_id(C_STRING_FILE, PLAYER_GM_GRANT_REWARD));			
			npcSetConversationResponses(player, responses.toArray(new string_id[0]));			
			return SCRIPT_CONTINUE;
		}
		
		if (response.equals(PLAYER_RESPONSE_HAVE_PROTOTYPE))
		{			
			utils.removeScriptVar(player, BRANCH_ID);
			doAnimationAction(npc, "blame");                
			string_id message = new string_id(C_STRING_FILE, NPC_RESPONSE_RECOVER_BROKEN);
			npcEndConversationWithMessage(player, message);			
			groundquests.sendSignal(player, QUEST_COLLECT_REWARD_SIGNAL);			
			createObjectInInventoryAllowOverload(QUEST_REWARD_ITEM, player);
			return SCRIPT_CONTINUE;
		}
	
	
		return SCRIPT_CONTINUE;
	}
	
	private int ansco_employee_eisley_NPC_QuestCompleted(obj_id player, obj_id npc, string_id response) throws InterruptedException
	{

		 ArrayList<string_id> responses = new ArrayList<string_id>();	
	
		if (response.equals(PLAYER_GM_TEST_MENU))
		{
			utils.setScriptVar(player, BRANCH_ID, C_BRANCH_PLAYER_GM_MENU);
			string_id message = new string_id(C_STRING_FILE, NPC_RESPONSE_GM_MENU);
			npcSpeak(player, message);			
			responses.add(new string_id(C_STRING_FILE, PLAYER_GM_CLEAR_QUEST));
			responses.add(new string_id(C_STRING_FILE, PLAYER_GM_GRANT_REWARD));			
			npcSetConversationResponses(player, responses.toArray(new string_id[0]));			

		}

		return SCRIPT_CONTINUE;		
	}
    
    
    private int ansco_employee_eisley_branch_opening_complaint_handler(obj_id player, obj_id npc, string_id response) throws InterruptedException
    {   
        //player is a GM, asks about the problem, says no time, stops conversing		


        ArrayList<string_id> responses = new ArrayList<string_id>();
		string_id message;


		if (response.equals(PLAYER_GM_TEST_MENU))
		{
			utils.setScriptVar(player, BRANCH_ID, C_BRANCH_PLAYER_GM_MENU);
			message = new string_id(C_STRING_FILE, NPC_RESPONSE_GM_MENU);
			npcSpeak(player, message);			
			responses.add(new string_id(C_STRING_FILE, PLAYER_GM_CLEAR_QUEST));
			responses.add(new string_id(C_STRING_FILE, PLAYER_GM_GRANT_REWARD));			
			npcSetConversationResponses(player, responses.toArray(new string_id[0]));			
			return SCRIPT_CONTINUE;
		}


		if (response.equals(PLAYER_RESPONSE_ASK_PROBLEM))
		{		
			utils.setScriptVar(player, BRANCH_ID, C_BRANCH_PLAYER_ASK_PROBLEM);
			message = new string_id(C_STRING_FILE, NPC_RESPONSE_PROBLEM);
			npcSpeak(player, message);			
			responses.add(new string_id(C_STRING_FILE, PLAYER_RESPONSE_OFFER_HELP));
			responses.add(new string_id(C_STRING_FILE, PLAYER_RESPONSE_REFUSE_NO_TIME));			
			npcSetConversationResponses(player, responses.toArray(new string_id[0]));
			return SCRIPT_CONTINUE;
		}

		if (response.equals(PLAYER_RESPONSE_REFUSE_NO_TIME))
		{
			utils.removeScriptVar(player, BRANCH_ID);
			doAnimationAction(npc, "shakefist");                
			message = new string_id(C_STRING_FILE, NPC_RESPONSE_GET_LOST);
			npcEndConversationWithMessage(player, message);
			return SCRIPT_CONTINUE;
		}

 		return SCRIPT_DEFAULT;
		
    }
    
    
 	private int ansco_employee_eisley_branch_ask_problem_handler(obj_id player, obj_id npc, string_id response) throws InterruptedException	
	{
		
		string_id message;	

		if (response.equals(PLAYER_RESPONSE_OFFER_HELP))
		{
			utils.removeScriptVar(player, BRANCH_ID);
			doAnimationAction(npc, "thank");                
			message = new string_id(C_STRING_FILE, NPC_RESPONSE_ACCEPT_HELP);
			npcEndConversationWithMessage(player, message);			
			groundquests.grantQuest(player, QUEST_NAME);
			return SCRIPT_CONTINUE;
		}	
		
	
		if (response.equals(PLAYER_RESPONSE_REFUSE_NO_TIME))
		{
			utils.removeScriptVar(player, BRANCH_ID);
			doAnimationAction(npc, "shakefist");                
			message = new string_id(C_STRING_FILE, NPC_RESPONSE_GET_LOST);
			npcEndConversationWithMessage(player, message);
			return SCRIPT_CONTINUE;
		}	
	
		return SCRIPT_DEFAULT;
	}
	
	private int ansco_employee_eisley_branch_gm_menu(obj_id player, obj_id npc, string_id response) throws InterruptedException	
	{
	
		string_id message;	

		if (response.equals(PLAYER_GM_CLEAR_QUEST))
		{
			utils.removeScriptVar(player, BRANCH_ID);
			
			if (ansco_employee_eisley_condition_activeorCompletedQuest(player, npc))
			{
				 groundquests.clearQuest(player, QUEST_NAME);
			}			
						
			message = new string_id(C_STRING_FILE, NPC_RESPONSE_GM_QUEST_CLEARED);
			npcEndConversationWithMessage(player, message);		
			return SCRIPT_CONTINUE;
		}		

		if (response.equals(PLAYER_GM_GRANT_REWARD))
		{
			utils.removeScriptVar(player, BRANCH_ID);			
			createObjectInInventoryAllowOverload(QUEST_REWARD_ITEM, player);			
			message = new string_id(C_STRING_FILE, NPC_RESPONSE_GM_QUEST_REWARD);			
			npcEndConversationWithMessage(player, message);		
			return SCRIPT_CONTINUE;
		}			
	
		return SCRIPT_DEFAULT;
	}
	
	
    //Quest State Methods
    
    
    private boolean ansco_employee_eisley_condition_completedQuest(obj_id player, obj_id npc) throws InterruptedException
    {
        if (groundquests.hasCompletedQuest(player, QUEST_NAME))
        {
            return true;
        }
        else 
        {
            return false;
        }
    }
    
    private boolean ansco_employee_eisley_condition_activeQuest(obj_id player, obj_id npc) throws InterruptedException
    {
	
        if (groundquests.isQuestActive(player, QUEST_NAME))
        {
            return true;
        }
        else 
        {
            return false;
        }
    }    
	
    private boolean ansco_employee_eisley_condition_activeorCompletedQuest(obj_id player, obj_id npc) throws InterruptedException
    {
        if (groundquests.isQuestActiveOrComplete(player, QUEST_NAME))
        {
            return true;
        }
        else 
        {
            return false;
        }
    }    

	
    
}
