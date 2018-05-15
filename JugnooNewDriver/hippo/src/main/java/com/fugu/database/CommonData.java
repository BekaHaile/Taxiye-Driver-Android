package com.fugu.database;

import android.content.Context;
import android.content.pm.PackageManager;

import com.fugu.HippoColorConfig;
import com.fugu.FuguFontConfig;
import com.fugu.adapter.ListItem;
import com.fugu.model.FuguConversation;
import com.fugu.model.FuguDeviceDetails;
import com.fugu.model.FuguGetMessageResponse;
import com.fugu.model.FuguPutUserDetailsResponse;
import com.fugu.model.Message;
import com.fugu.utils.FuguLog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import io.paperdb.Paper;

/**
 * Created by Bhavya Rattan on 15/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public final class CommonData implements PaperDbConstant {

    public static List<FuguConversation> CONVERSATION_LIST = Collections.emptyList();
    public static FuguPutUserDetailsResponse USER_DETAILS = null;
    public static String SERVER_URL = "";
    public static String APP_SECRET_KEY = "";
    public static int APP_TYPE = 1;
    public static TreeMap<Long, FuguGetMessageResponse> GET_MESSAGE_RESPONSE_MAP = new TreeMap<>();
    public static TreeMap<Long, FuguGetMessageResponse> GET_LABEL_ID_RESPONSE_MAP = new TreeMap<>();
    public static TreeMap<Long, TreeMap<String, ListItem>> UNSENT_MESSAGE_MAP = new TreeMap<>();
    public static HippoColorConfig COLOR_CONFIG = new HippoColorConfig();
    public static FuguFontConfig FONT_CONFIG = new FuguFontConfig();
    public static String isNewChatKey = "IS_NEW_CHAT";
    public static String providerKey = "PROVIDER_KEY";
    public static String pushKey = "PUSH_KEY";
    public static String pushChannelKey = "PUSH_CHANNEL_KEY";
    public static String isAppOpenKey = "isAppOpen";
    public static String newPeerChatCreated = "FUGU_NEW_PEER_CHAT_CREATED";
    public static TreeMap<Long, ArrayList<ListItem>> FUGU_MESSAGE_LIST = new TreeMap<>();
    public static final String clearFuguDataKey = "clearHippoData";

    /**
     * Empty Constructor
     * not called
     */
    private CommonData() {
    }


    /**
     * Save PAPER_CONVERSATION_LIST
     *
     * @param getMessageResponse
     */

    public static void setMessageResponse(Long channelId, FuguGetMessageResponse getMessageResponse) {
        GET_MESSAGE_RESPONSE_MAP.put(channelId, getMessageResponse);
        Paper.book().write(PAPER_GET_MESSAGE_RESPONSE_MAP, CommonData.GET_MESSAGE_RESPONSE_MAP);
    }

//    public static void setFuguMessageList(Long channelId, ArrayList<ListItem> fuguMessageList) {
//        FUGU_MESSAGE_LIST.put(channelId, fuguMessageList);
//        Paper.book().write(PAPER_FUGU_MESSAGE_LIST, CommonData.FUGU_MESSAGE_LIST);
//    }


    /**
     * Save PAPER_GET_MESSAGE_RESPONSE_MAP
     *
     * @param channelId
     * @param messages
     */

    public static void setMessagesToMessageMap(Long channelId, ArrayList<Message> messages) {
        if (GET_MESSAGE_RESPONSE_MAP.get(channelId) != null && GET_MESSAGE_RESPONSE_MAP.get(channelId).getData() != null) {
            GET_MESSAGE_RESPONSE_MAP.get(channelId).getData().setMessages(messages);
            Paper.book().write(PAPER_GET_MESSAGE_RESPONSE_MAP, CommonData.GET_MESSAGE_RESPONSE_MAP);
        }
    }

    /**
     * Save PAPER_GET_LABEL_ID_RESPONSE_MAP
     *
     * @param channelId
     * @param messages
     */

    public static void setMessagesToLabelMap(Long channelId, ArrayList<Message> messages) {
        if (GET_LABEL_ID_RESPONSE_MAP.get(channelId) != null && GET_LABEL_ID_RESPONSE_MAP.get(channelId).getData() != null) {
            GET_LABEL_ID_RESPONSE_MAP.get(channelId).getData().setMessages(messages);
            Paper.book().write(PAPER_GET_LABEL_ID_RESPONSE_MAP, CommonData.GET_LABEL_ID_RESPONSE_MAP);
        }
    }

    /**
     * Gets PAPER_GET_MESSAGE_RESPONSE_MAP
     *
     * @return the messageResponse
     */

    public static FuguGetMessageResponse getMessageResponse(Long channelId) {
        if (GET_MESSAGE_RESPONSE_MAP.isEmpty()) {
            GET_MESSAGE_RESPONSE_MAP = Paper.book().read(PAPER_GET_MESSAGE_RESPONSE_MAP, new TreeMap<Long, FuguGetMessageResponse>());
        }
        return GET_MESSAGE_RESPONSE_MAP.get(channelId);
    }

//    public static ArrayList<ListItem> getFuguMessageList(Long channelId) {
//        if (FUGU_MESSAGE_LIST.isEmpty()) {
//            FUGU_MESSAGE_LIST = Paper.book().read(PAPER_FUGU_MESSAGE_LIST, new TreeMap<Long, ArrayList<ListItem>>());
//        }
//        return FUGU_MESSAGE_LIST.get(channelId);
//    }

    /**
     * Save PAPER_CONVERSATION_LIST
     *
     * @param getByLabelIdResponse
     */

    public static void setLabelIdResponse(Long labelId, FuguGetMessageResponse getByLabelIdResponse) {
        GET_LABEL_ID_RESPONSE_MAP.put(labelId, getByLabelIdResponse);
        Paper.book().write(PAPER_GET_LABEL_ID_RESPONSE_MAP, CommonData.GET_LABEL_ID_RESPONSE_MAP);
    }

    /**
     * Gets PAPER_CONVERSATION_LIST
     *
     * @return the conversationList
     */

    public static FuguGetMessageResponse getLabelIdResponse(Long labelId) {
        if (GET_LABEL_ID_RESPONSE_MAP.isEmpty()) {
            GET_LABEL_ID_RESPONSE_MAP = Paper.book().read(PAPER_GET_LABEL_ID_RESPONSE_MAP, new TreeMap<Long, FuguGetMessageResponse>());
        }
        return GET_LABEL_ID_RESPONSE_MAP.get(labelId);
    }


    /**
     * Save PAPER_SERVER_URL
     *
     * @param serverUrl
     */

    public static void setServerUrl(String serverUrl) {
        SERVER_URL = serverUrl;
        Paper.book().write(PAPER_SERVER_URL, CommonData.SERVER_URL);
    }

    /**
     * Gets PAPER_CONVERSATION_LIST
     *
     * @return the serverUrl
     */

    public static String getServerUrl() {
        if (SERVER_URL.isEmpty()) {
            SERVER_URL = Paper.book().read(PAPER_SERVER_URL, "");
        }
        return SERVER_URL;
    }

    public static void setCurrentVersion(int versionCode) {
        Paper.book().write(PAPER_DB_VERSION, versionCode);
    }

    public static int getLocalVersion() {
        return Paper.book().read(PAPER_DB_VERSION, -1);
    }

    public static void setDefaultCategory(String defaultCategory) {
        Paper.book().write(PAPER_USER_DEFAULT_CATEGORY, defaultCategory);
    }

    public static String getDefaultCategory() {
        return Paper.book().read(PAPER_USER_DEFAULT_CATEGORY, null);
    }

    /**
     * Save PAPER_APP_SECRET_KEY
     *
     * @param appSecretKey
     */

    public static void setAppSecretKey(String appSecretKey) {
        APP_SECRET_KEY = appSecretKey;
        Paper.book().write(PAPER_APP_SECRET_KEY, CommonData.APP_SECRET_KEY);
    }

    /**
     * Gets PAPER_APP_SECRET_KEY
     *
     * @return the appSecretKey
     */

    public static String getAppSecretKey() {
        if (APP_SECRET_KEY.isEmpty()) {
            APP_SECRET_KEY = Paper.book().read(PAPER_APP_SECRET_KEY, "");
        }
        return APP_SECRET_KEY;
    }


    /**
     * Save PAPER_APP_SECRET_KEY
     *
     * @param appType
     */

    public static void setAppType(int appType) {
        APP_TYPE = appType;
        Paper.book().write(PAPER_APP_TYPE, CommonData.APP_TYPE);
    }

    /**
     * Gets PAPER_APP_SECRET_KEY
     *
     * @return the appSecretKey
     */

    public static int getAppType() {
        APP_TYPE = Paper.book().read(PAPER_APP_TYPE, 1);
        return APP_TYPE;
    }

    /*
    * Sets cachedMessages in getMessagesResponse or getLabelIdResponse
    *
    * @param cachedMessages
    * */

    public static void setCachedMessages(boolean isOpenChat, Long mapKey, ArrayList<Message> cachedMessages) {
        if (isOpenChat && GET_LABEL_ID_RESPONSE_MAP.get(mapKey) != null &&
                GET_LABEL_ID_RESPONSE_MAP.get(mapKey).getData() != null) {
            GET_LABEL_ID_RESPONSE_MAP.get(mapKey).getData().getMessages().clear();
            GET_LABEL_ID_RESPONSE_MAP.get(mapKey).getData().getMessages().addAll(cachedMessages);
        } else if (GET_MESSAGE_RESPONSE_MAP.get(mapKey) != null && GET_MESSAGE_RESPONSE_MAP.get(mapKey).getData() != null) {
            GET_MESSAGE_RESPONSE_MAP.get(mapKey).getData().getMessages().clear();
            GET_MESSAGE_RESPONSE_MAP.get(mapKey).getData().getMessages().addAll(cachedMessages);
        }
    }

    public static void setSupportPath(ArrayList<String> pathList) {
        Paper.book().write(PAPER_SUPPORT_PATH, pathList);
        FuguLog.d("TAG", "Path = "+new Gson().toJson(pathList));
    }

    public static ArrayList<String> getPathList() {
        return Paper.book().read(PAPER_SUPPORT_PATH, new ArrayList<String>());
    }

    public static void removeLastPath() {
        ArrayList<String> pathList = getPathList();
        pathList.remove(pathList.size()-1);
        setSupportPath(pathList);
    }

    public static void clearPathList() {
        Paper.book().delete(PAPER_SUPPORT_PATH);
    }

    public static void saveUserUniqueKey(String userUniqueKey) {
        Paper.book().write(PAPER_USER_UNIQUE_KEY, userUniqueKey);
    }

    public static String getUserUniqueKey() {
        return Paper.book().read(PAPER_USER_UNIQUE_KEY);
    }


    /**
     * Save PAPER_CONVERSATION_LIST
     *
     * @param conversationList
     */

    public static void setConversationList(List<FuguConversation> conversationList) {
        CommonData.CONVERSATION_LIST = conversationList;
        Paper.book().write(PAPER_CONVERSATION_LIST, conversationList);
    }

    /**
     * Gets PAPER_CONVERSATION_LIST
     *
     * @return the conversationList
     */

    public static List<FuguConversation> getConversationList() {
        if (CONVERSATION_LIST.isEmpty())
            CONVERSATION_LIST = Paper.book().read(PAPER_CONVERSATION_LIST, Collections.<FuguConversation>emptyList());
        return CONVERSATION_LIST;
    }

    /**
     * Gets PAPER_USER_DETAILS
     *
     * @return the userDetails
     */

    public static FuguPutUserDetailsResponse getUserDetails() {
        if (USER_DETAILS == null)
            USER_DETAILS = Paper.book().read(PAPER_USER_DETAILS, null);
        return USER_DETAILS;
    }

    /**
     * Save PAPER_COLOR_CONFIG
     *
     * @param hippoColorConfig
     */
    public static void setColorConfig(HippoColorConfig hippoColorConfig) {
        CommonData.COLOR_CONFIG = hippoColorConfig;
        Paper.book().write(PAPER_COLOR_CONFIG, hippoColorConfig);
    }

    /**
     * Save PAPER_FONT_CONFIG
     *
     * @param fuguFontConfig font config
     */
    public static void setFontConfig(FuguFontConfig fuguFontConfig) {
        CommonData.FONT_CONFIG = fuguFontConfig;
        Paper.book().write(PAPER_FONT_CONFIG, fuguFontConfig);
    }

    /**
     * Gets PAPER_COLOR_CONFIG
     *
     * @return the fuguColorConfig
     */

    public static HippoColorConfig getColorConfig() {
        if (COLOR_CONFIG == null)
            COLOR_CONFIG = Paper.book().read(PAPER_COLOR_CONFIG, null);
        return COLOR_CONFIG;
    }

    /**
     * Gets PAPER_FONT_CONFIG
     *
     * @return the fuguFontConfig
     */

    public static FuguFontConfig getFontConfig() {
        if (FONT_CONFIG == null)
            FONT_CONFIG = Paper.book().read(PAPER_FONT_CONFIG, null);
        return FONT_CONFIG;
    }

    /**
     * Save PAPER_USER_DETAILS
     *
     * @param userDetails
     */
    public static void setUserDetails(FuguPutUserDetailsResponse userDetails) {
        CommonData.USER_DETAILS = userDetails;
        Paper.book().write(PAPER_USER_DETAILS, userDetails);
        //setCurrentVersion(userDetails.getData().getInAppSupportVersion());
    }


    public static void setUnsentMessageMapByChannel(Long channelId, TreeMap<String, ListItem> unsentMessageMap) {
        UNSENT_MESSAGE_MAP.put(channelId, unsentMessageMap);
        Paper.book().write(PAPER_UNSENT_MESSAGE_MAP, CommonData.UNSENT_MESSAGE_MAP);
    }

    public static void removeUnsentMessageMapChannel(Long channelId) {
        CommonData.UNSENT_MESSAGE_MAP.remove(channelId);
        Paper.book().write(PAPER_UNSENT_MESSAGE_MAP, UNSENT_MESSAGE_MAP);
    }

    /**
     * Gets PAPER_UNSENT_MESSAGE_MAP
     *
     * @return the unsentMessageMap
     */

    public static TreeMap<String, ListItem> getUnsentMessageMapByChannel(Long channelId) {
        if (UNSENT_MESSAGE_MAP.isEmpty()) {
            UNSENT_MESSAGE_MAP = Paper.book().read(PAPER_UNSENT_MESSAGE_MAP, new TreeMap<Long, TreeMap<String, ListItem>>());
        }
        return UNSENT_MESSAGE_MAP.get(channelId);
    }

    public static TreeMap<Long, TreeMap<String, ListItem>> getUnsentMessageMap() throws Exception {
        if (UNSENT_MESSAGE_MAP.isEmpty()) {
            UNSENT_MESSAGE_MAP = Paper.book().read(PAPER_UNSENT_MESSAGE_MAP, new TreeMap<Long, TreeMap<String, ListItem>>());
        }
        return UNSENT_MESSAGE_MAP;
    }


    //======================================== Clear Data ===============================================

    /**
     * Delete paper.
     */
    public static void clearData() throws Exception {
        USER_DETAILS = null;
        CONVERSATION_LIST = Collections.emptyList();
        GET_MESSAGE_RESPONSE_MAP = new TreeMap<>();
        GET_LABEL_ID_RESPONSE_MAP = new TreeMap<>();
        UNSENT_MESSAGE_MAP = new TreeMap<>();
        SERVER_URL = "";
        APP_SECRET_KEY = "";
        COLOR_CONFIG = new HippoColorConfig();
        APP_TYPE = 1;
        Paper.book().destroy();
    }

    public static JsonObject deviceDetails(Context context) {
        Gson gson = new GsonBuilder().create();
        JsonObject deviceDetailsJson = null;
        try {
            deviceDetailsJson = gson.toJsonTree(new FuguDeviceDetails(
                    getAppVersion(context)).getDeviceDetails()).getAsJsonObject();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return deviceDetailsJson;
    }


    public static int getAppVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
            FuguLog.e("IP Address", ex.toString());
        }
        return null;
    }

    public static void setIsNewchat(boolean isNewchat) {
        Paper.book().write(isNewChatKey, isNewchat);
    }

    public static boolean getIsNewChat() {
        return Paper.book().read(isNewChatKey);
    }

    public static void setNewPeerChatCreated(boolean isNewPeerChatCreated) {
        Paper.book().write(newPeerChatCreated, isNewPeerChatCreated);
    }

    public static boolean getNewPeerChatCreated() {
        return Paper.book().read(newPeerChatCreated,false);
    }

    public static void setProvider(String provider) {
        Paper.book().write(providerKey, provider);
    }

    public static String getProvider() {
        return Paper.book().read(providerKey);
    }

    public static void setPushBoolean(boolean push) {
        Paper.book().write(pushKey, push);
    }

    public static boolean getPushBoolean() {
        if (Paper.book().read(pushKey) == null) {
            return false;
        }
        return Paper.book().read(pushKey);
    }

    public static void setPushChannel(Long pushChannel) {
        Paper.book().write(pushChannelKey, pushChannel);
    }

    public static Long getPushChannel() {
        if (Paper.book().read(pushChannelKey) == null) {
            return -1l;
        }
        return Paper.book().read(pushChannelKey);
    }

    public static void clearPushChannel() {
        Paper.book().delete(pushChannelKey);
    }

    public static void setTransactionIdsMap(HashMap<String, Long> transactionIdsMap) {
        Paper.book().write("TransactionIdsMap", transactionIdsMap);
    }

    public static HashMap<String, Long> getTransactionIdsMap() {
        return Paper.book().read("TransactionIdsMap");
    }

    public static boolean getIsAppOpen() {
        if (Paper.book().read(isAppOpenKey) == null) {
            return true;
        }
        return Paper.book().read(isAppOpenKey);
    }

    public static void setIsAppOpen(boolean isAppOpen) {
        Paper.book().write(isAppOpenKey, isAppOpen);
    }

    public static void setClearFuguDataKey(boolean clearFuguData) {
        Paper.book().write(clearFuguDataKey, clearFuguData);
    }

    public static boolean getClearFuguDataKey() {
        return Paper.book().read(clearFuguDataKey, false);
    }
}
