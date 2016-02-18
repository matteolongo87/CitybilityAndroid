package com.citybility.app.util;

public class Constant {


	public static final int PROVIDER_NONE 			= -1;
	public static final int PROVIDER_CITYBILITY 	= 0;
	public static final int PROVIDER_TWITTER 		= 1;
	public static final int PROVIDER_FACEBOOK 		= 2;
	public static final int PROVIDER_GOOGLE 		= 3;
	public static final int PROVIDER_MICROSOFT 		= 4;
	public static final int PROVIDER_LINKEDIN 		= 5;
	public static final int PROVIDER_INSTAGRAM 		= 6;

	public static final int PICTURE_TAKEN_FROM_CAMERA = 101;
	public static final int PICTURE_TAKEN_FROM_GALLERY =102;
		
	public static final String PRIVACY_POLICY_URL = "http://www.iubenda.com/privacy-policy/252211/legal";

	public static int DEFAULT_DISTANCE = 9999999;
	public static int DEFAULT_NUM_TO_TAKE = 10;

	// Notifiche GCM
	// FOR TEST public static final String SENDER_ID = "426233059186"; //Google Cloud Messaging Server key
	public static final String SENDER_ID = "791713913879"; //Google Cloud Messaging Server key
	public static final String NOTIFICATION_SERVER_URL_ = "";
	public static final long NOTIFICATION_BADGE_UPDATING_INTERVAL = 30000; // milliseconds
	
	public static final int SQUARE_THUMBNAIL_DIM = 300;
	
	public static final String ACTION_LOGOUT = "action.logout";
	
	public static final int USER_MESSAGGES_STATUS_ALL = -1;
	public static final int USER_MESSAGGES_STATUS_NEW = 0;
	public static final int USER_MESSAGGES_STATUS_READ = 1;
	
	public static final String SHOW_INTRO_PARAM = "showIntro";
	
	public static enum MessageType {
		ALL(-1), TRANSSACTION(0), INFORMATION(1), ERROR(2), USER(3), LOCATION(4), INITIATIVE(5);

		private final long type;
		private MessageType(int type){
			this.type = type;
		}
		
		public long getType() {
			return type;
		}
		
	}
	
	public static enum CampaignFilter {
		HOME(0), NEARBY(1), RECENTS(2), CATEGORY(3);

		private final int mode;

		private CampaignFilter(int value) {
			this.mode = value;
		}

		public int getMode() {
			return mode;
		}
	}

	public static enum CampaignOrdering {
		HOME(0), PROXIMITY(1), RECENTS(2);

		private final int order;

		private CampaignOrdering(int value) {
			this.order = value;
		}

		public int getOrder() {
			return order;
		}
	}

	public static enum ProjectFilter {
		HOME(0), NEARBY(1), RECENTS(2);

		private final int mode;

		private ProjectFilter(int value) {
			this.mode = value;
		}

		public int getMode() {
			return mode;
		}
	}

	public static enum ProjectOrdering {
		PROXIMITY(0), RECENTS(1), POPULAR(2);

		private final int order;

		private ProjectOrdering(int value) {
			this.order = value;
		}

		public int getOrder() {
			return order;
		}
	}

	public static enum CitybiliterFilter {
		ACTIVITY(0), PROXIMITY(1);

		private final int mode;

		private CitybiliterFilter(int value) {
			this.mode = value;
		}

		public int getMode() {
			return mode;
		}
	}


	public static enum CitybiliterOrdering {
		ACTIVITY(0), PROXIMITY(1);

		private final int order;

		private CitybiliterOrdering(int value) {
			this.order = value;
		}

		public int getOrder() {
			return order;
		}
	}
}
