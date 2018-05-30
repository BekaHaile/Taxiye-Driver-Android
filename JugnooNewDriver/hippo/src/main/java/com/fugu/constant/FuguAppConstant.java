package com.fugu.constant;

import android.os.Environment;

import java.io.File;

/**
 * Created by Bhavya Rattan on 10/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public interface FuguAppConstant {

    String DEV_SERVER = "https://alpha-api.fuguchat.com";
    //String TEST_SERVER = "https://beta-api.fuguchat.com"; // 13.126.0.203:3001
    String TEST_SERVER = "https://hippo-api-dev.fuguchat.com:3011";
    String BETA_LIVE_SERVER = "https://api.fuguchat.com";
    String LIVE_SERVER = "https://api.fuguchat.com";

    int SESSION_EXPIRE = 403;
    int DATA_UNAVAILABLE = 406;
    String NETWORK_STATE_INTENT = "network_state_changed";
    String NOTIFICATION_INTENT = "notification_received";
    String NOTIFICATION_TAPPED = "notification_tapped";
    String FUGU_WEBSITE_URL = "https://jungleworks.co/hippo";

    int IMAGE_MESSAGE = 10;
    int FILE_MESSAGE = 11;
    int ACTION_MESSAGE = 12;
    int TEXT_MESSAGE = 1;
    int READ_MESSAGE = 6;

    int ANDROID_USER = 1;

    int CHANNEL_SUBSCRIBED = 1;
    int CHANNEL_UNSUBSCRIBED = 0;

    int STATUS_CHANNEL_CLOSED = 0;
    int STATUS_CHANNEL_OPEN = 1;

    int TYPING_SHOW_MESSAGE = 0;
    int TYPING_STARTED = 1;
    int TYPING_STOPPED = 2;

    int MESSAGE_SENT = 1;
    int MESSAGE_DELIVERED = 2;
    int MESSAGE_READ = 3;
    int MESSAGE_UNSENT = 4;
    int MESSAGE_IMAGE_RETRY = 5;
    int MESSAGE_FILE_RETRY = 6;

    int PERMISSION_CONSTANT_CAMERA = 9;
    int PERMISSION_CONSTANT_GALLERY = 8;
    int PERMISSION_READ_IMAGE_FILE = 4;
    int PERMISSION_SAVE_BITMAP = 5;
    int PERMISSION_READ_FILE = 6;


    int OPEN_CAMERA_ADD_IMAGE = 514;
    int OPEN_GALLERY_ADD_IMAGE = 515;
    int SELECT_FILE = 516;

    //Notification Type
    int NOTIFICATION_DEFAULT = -1;
    int NOTIFICATION_READ_ALL = 6;

    // action
    String FUGU_CUSTOM_ACTION_SELECTED = "FUGU_CUSTOM_ACTION_SELECTED";
    String FUGU_CUSTOM_ACTION_PAYLOAD = "FUGU_CUSTOM_ACTION_PAYLOAD";

    String IMAGE_DIRECTORY = Environment.getExternalStorageDirectory() + File.separator + "fugu" +
            File.separator + "picture";
    String CONVERSATION = "conversation";
    String NOTIFICATION_TYPE = "notification_type";
    String USER_ID = "user_id";
    String EN_USER_ID = "en_user_id";
    String PEER_CHAT_PARAMS = "peer_chat_params";
    String APP_SECRET_KEY = "app_secret_key";
    String DEVICE_TYPE = "device_type";
    String DEVICE_DETAILS = "device_details";
    String RESELLER_TOKEN = "reseller_token";
    String REFERENCE_ID = "reference_id";
    String DEVICE_ID = "device_id";
    String APP_TYPE = "app_type";
    String APP_VERSION="app_version";
    String ANDROID="Android";
    String USER_UNIQUE_KEY = "user_unique_key";
    String FULL_NAME = "full_name";
    String MESSAGE = "message";
    String NEW_MESSAGE = "new_message";
    String MESSAGE_STATUS = "message_status";
    String MESSAGE_INDEX = "message_index";
    String MESSAGE_TYPE = "message_type";
    String USER_TYPE = "user_type";
    String DATE_TIME = "date_time";
    String EMAIL = "email";
    String PHONE_NUMBER = "phone_number";
    String DEVICE_TOKEN = "device_token";
    String IS_TYPING = "is_typing";
    String CHANNEL_ID = "channel_id";
    String UNREAD_COUNT = "unread_count";
    String ON_SUBSCRIBE = "on_subscribe";
    String IMAGE_URL = "image_url";
    String THUMBNAIL_URL = "thumbnail_url";
    String ADDRESS = "address";
    String LAT_LONG = "lat_long";
    String IP_ADDRESS = "ip_address";
    String ATTRIBUTES = "attributes";
    String CUSTOM_ATTRIBUTES = "custom_attributes";
    String ISP2P = "isP2P";
    String CHAT_TYPE = "chat_type";
    String ERROR = "error";
    String INTRO_MESSAGE = "intro_message";
    String CUSTOM_ACTION = "custom_action";

    String SUPPORT_ID = "support_id";
    String SUPPORT_TRANSACTION_ID = "support_transaction_id";
    String HIPPO_SUPPORT_TAGS = "support_tags";
    String SUPPORT_IS_ACTIVE = "is_active";
    String SOURCE_KEY = "source";

    /**
     * The type of file being Saved
     */
    enum FileType {

        LOG_FILE("logs", ".log"),
        IMAGE_FILE("snapshots", ".jpg"),
        GENERAL_FILE("public", ".txt"),
        PDF_FILE("public", ".pdf"),
        PRIVATE_FILE("system", ".sys");

        public final String extension;
        public final String directory;

        FileType(String relativePath, String extension) {
            this.extension = extension;
            this.directory = relativePath;
        }
    }
}
