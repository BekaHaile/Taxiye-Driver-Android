package product.clicklabs.jugnoo.driver;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.ContactBean;
import product.clicklabs.jugnoo.driver.utils.ContactsEntityBean;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by socomo on 11/19/15.
 */
public class ContactsUploadService extends IntentService {

    private static final String TAG = ContactsUploadService.class.getSimpleName();
    private static int UPLOAD_BATCH_SIZE = 100;
    private ArrayDeque<ContactSyncEntry> mSyncQueue;
    private String accessToken = "";

    public ContactsUploadService() {
        this("Contact Upload Service");
    }

    public ContactsUploadService(String name) {
        super(name);
    }



    @Override
    protected void onHandleIntent(Intent intent) {

        if(intent.hasExtra("access_token")){
            accessToken = intent.getExtras().get("access_token").toString();
        }

        Log.v("intent values are ", "--> " + accessToken);

        final Cursor contacts = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
                null, null);

        if (contacts.getCount() > 0) {
            mSyncQueue = new ArrayDeque<>(contacts.getCount() / UPLOAD_BATCH_SIZE + 1);
            //queueUpSyncs(contacts);
            newQueueUpSyncs();
        } else {
            mSyncQueue = new ArrayDeque<>();
        }
        contacts.close();
    }


    /**
     * Method that hashes contacts and syncs them to server
     *
     * @param contactsCursor The cursor for contacts
     */
    private void queueUpSyncs(final Cursor contactsCursor) {

        ContactSyncEntry syncEntry = null;
        while (contactsCursor.moveToNext()) {

            if (syncEntry == null) {
                syncEntry = new ContactSyncEntry(UPLOAD_BATCH_SIZE);
            }

            final String number = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            final String name = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            Log.d(TAG, name+" = "+number);

            if (!TextUtils.isEmpty(number)) {
                syncEntry.addNumber(number);
                syncEntry.addName(name);

                if (syncEntry.isFull()) {
                    Log.d(TAG, "Adding sync entry to queue");
                    mSyncQueue.add(syncEntry);
                    syncEntry = null;
                }
            }
        }

        Log.d(TAG, "mSyncQueue size = " + mSyncQueue.size());

    }

    private void newQueueUpSyncs() {
        try {
            ArrayList<ContactBean> contactList = new ArrayList<ContactBean>();
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);

            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String email = "null";
                    if (Integer.parseInt(cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                new String[]{id}, null);

                        while (pCur.moveToNext()) {
                            ContactBean contactBean = new ContactBean();

                            // Log.e("Name :", name);
                            String phone = pCur
                                    .getString(pCur
                                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phone = phone.replace(" ","");

                            contactBean.setName(name);
                            contactBean.setPhone(phone);
                            contactBean.setEmail(email);
                            if (phone != null && (phone.length() >= 10)) {
                                contactList.add(contactBean);
                            }
                        }
                        pCur.close();
                    }
                }
            }
            cur.close();

            /*ContentResolver cr1 = getContentResolver();
            Cursor emailCur = cr1.query(ContactsContract.Contacts.CONTENT_URI, null,
                    null, null, null);

            if (emailCur.getCount() > 0) {
                while (emailCur.moveToNext()) {
                    String id = emailCur.getString(emailCur.getColumnIndex(ContactsContract.Contacts._ID));
                    Cursor cur1 = cr1.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Email.CONTACT_ID
                                    + " = ?", new String[]{
                                    id
                            }, null);
                    String phone = "null";
                    while (cur1.moveToNext()) {
                        ContactBean contactBean = new ContactBean();
                        // to get the contact names
                        String name = cur1
                                .getString(cur1
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                        String email = cur1
                                .getString(cur1
                                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));

                        contactBean.setName(name);
                        contactBean.setPhone(phone);
                        contactBean.setEmail(email);
                        if (email != null) {
                            contactList.add(contactBean);
                        }
                    }
                    cur1.close();
                }
            }
            emailCur.close();*/


            Log.v("size is ", "---> " + contactList.size());
            /*for(int i=0; i<contactList.size(); i++){
                Log.v("name ", contactList.get(i).getName().toString()+", Phone "+contactList.get(i).getPhone().toString()
                +", Email "+contactList.get(i).getEmail().toString());
            }*/

            loadList(contactList);

            //getContactDetails();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void loadList(ArrayList<ContactBean> list) {

        Set set = new TreeSet(new Comparator<ContactBean>() {
            @Override
            public int compare(ContactBean o1, ContactBean o2) {
                if(o1.getName().toString().equalsIgnoreCase(o2.getName().toString())){
                    return 0;
                }
                return 1;
            }
        });

        set.addAll(list);

        System.out.println("\n***** After removing duplicates *******\n");

        final ArrayList<ContactBean> newList = new ArrayList<ContactBean>(set);
        Log.v("newList size", "--->" + newList.size());
        UPLOAD_BATCH_SIZE = newList.size(); // here I set size of upload batch. right now its full length

        ContactSyncEntry syncEntry = null;
        for(int i=0; i<newList.size(); i++){
            Log.v("name ", newList.get(i).getName().toString()+", Phone "+newList.get(i).getPhone().toString()+", Email "+newList.get(i).getEmail().toString());

            if (syncEntry == null) {
                syncEntry = new ContactSyncEntry(UPLOAD_BATCH_SIZE);
            }

            final String number = newList.get(i).getPhone();
            final String name = newList.get(i).getName();
            final String email = newList.get(i).getEmail();

            if (!TextUtils.isEmpty(number)) {
                syncEntry.addNumber(number);
                syncEntry.addName(name);
                syncEntry.addEmail(email);

                if (syncEntry.isFull() || (i == (newList.size()-1))) {
                    Log.d(TAG, "Adding sync entry to queue");
                    mSyncQueue.add(syncEntry);
                    syncEntry = null;
                }
            }
        }

        Log.d(TAG, "mSyncQueue size = " + mSyncQueue.size());
        syncQueue();
    }

    /**
     * Checks if all the pending syncs were successful
     */
    private void checkIfAllSynced() {

        boolean completedSync = true;
        for (ContactSyncEntry entry : mSyncQueue) {
            completedSync &= entry.isSynced();
        }

        if (completedSync) {
            Log.d(TAG, "SYNCED");
        }
    }

    private void doneWithSync() {
        Log.d(TAG, "STOP SERVICE");
        sendBroadcast(new Intent(Constants.ACTION_LOADING_COMPLETE));
        stopSelf();
    }

    /**
     * Syncs the queued up syncs with the server
     */
    private void syncQueue() {
        Log.d(TAG, "Pending Syncs: %d " + (mSyncQueue != null ? mSyncQueue.size() : 0));
        if (mSyncQueue != null && !mSyncQueue.isEmpty()) {
            try {
            for (ContactSyncEntry currentSyncEntry : mSyncQueue) {
                //currentSyncEntry.setSynced(true);
                Log.d(TAG, "Numbers to sync: %d "+ currentSyncEntry.numbersToSync.size());

                String contactName = "";
                String phoneNumber = "";
                String emailAddress = "";
                List<ContactBean> contactsList = new ArrayList<ContactBean>();
                Log.d(TAG, "Email size %d " + currentSyncEntry.emailToSync.size());

                for (int i = 0; i < currentSyncEntry.numbersToSync.size(); i++) {

                    ContactBean contacts = new ContactBean();
                    phoneNumber = currentSyncEntry.numbersToSync.get(i);


                    if (i < currentSyncEntry.nameToSync.size()){
                        contactName = currentSyncEntry.nameToSync.get(i);
                        emailAddress = currentSyncEntry.emailToSync.get(i);
                    }



                    if (!TextUtils.isEmpty(phoneNumber)) {
                        contacts.setPhone(phoneNumber);
                        contacts.setName(contactName);
                        contacts.setEmail(emailAddress);
                    }
                    contactsList.add(contacts);
                }


                Log.v("size is ","---> "+contactsList.size());
                /*for(int i=0; i<contactsList.size(); i++){
                    Log.v("name ", contactsList.get(i).getName().toString() + ", Phone " + contactsList.get(i).getPhone().toString()
                            + ", Email " + contactsList.get(i).getEmail().toString());
                }*/



                    JSONArray jsonArray = new JSONArray();

                    for (int i = 0; i < contactsList.size(); i++) {

                        jsonArray.put(contactsList.get(i).getPhone());
                    }

                    JSONObject contactsObj = new JSONObject();
                    contactsObj.put("all_contacts", jsonArray);

                    String jsonStr = contactsObj.toString();
                    System.out.println("jsonString: " + jsonStr);


                    // Call Api
                    uploadContactsApi(jsonStr, currentSyncEntry);
                    //doneWithSync();

            }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void uploadContactsApi(String requestParam, final ContactSyncEntry currentSyncEntry){
        HashMap<String, String> params = new HashMap<>();
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

            //DialogPopup.showLoadingDialog(this, "Loading...");
            params.put("access_token", accessToken);
            Log.i("access_token and session_id", accessToken);

            try {
                if (requestParam != null) {
                    params.put("contacts", requestParam);
                }
            } catch(Exception e){
                e.printStackTrace();
            }

            Log.i("params request_dup_registration", "=" + params);

            Response response = RestClient.getApiServices().sendAllContactsSync(params);
            if(response != null){
                try {
                    String responseStr = new String(((TypedByteArray)response.getBody()).getBytes());
                    Log.i(TAG, "referAllContactsSync response = " + responseStr);

                    try {
						JSONObject jObj = new JSONObject(responseStr);
						int flag = jObj.getInt("flag");
						String message = JSONParser.getServerMessage(jObj);
						if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
//							Data.userData.contactSaved = 1;
						}
					}  catch (Exception exception) {
						exception.printStackTrace();
						//DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
					}
                    //DialogPopup.dismissLoadingDialog();
                    currentSyncEntry.setSynced(true);
                    checkIfAllSynced();
                    doneWithSync();
                } catch (Exception e) {
                    e.printStackTrace();
                    doneWithSync();
                }
            } else{
                doneWithSync();
            }
        }
        else {
            //Database2.getInstance(ContactsUploadService.this).insertPendingAPICall(ContactsUploadService.this, Config.getServerUrl()+"/refer_all_contacts", params);
            //DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
        }
    }


    public ArrayList<ContactsEntityBean> getContactDetails() {
        ArrayList<ContactsEntityBean> contactList = new ArrayList<ContactsEntityBean>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor cur1 = cr.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Email.CONTACT_ID
                                + " = ?", new String[] {
                                id
                        }, null);
                while (cur1.moveToNext()) {
                    ContactsEntityBean contactsEntityBean = new ContactsEntityBean();
                    // to get the contact names
                    String name = cur1
                            .getString(cur1
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                    // Log.e("Name :", name);
                    String email = cur1
                            .getString(cur1
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    // Log.e("Email", email);


                    contactsEntityBean.setPhones(name);
                    contactsEntityBean.setEmails(email);
                    if (email != null) {
                        contactList.add(contactsEntityBean);
                    }
                }
                cur1.close();
            }
        }

        Log.v("size is ","---> "+contactList.size());
        for(int i=0; i<contactList.size(); i++){
            Log.v("name ", contactList.get(i).getPhones()+", email "+contactList.get(i).getEmails());
        }
        return contactList;
    }

    /**
     * Class that holds a single contact upload operation
     */
    private static class ContactSyncEntry {

        public final List<String> numbersToSync;
        public final List<String> nameToSync;
        public final List<String> emailToSync;


        public final int capacity;

        private boolean mSynced;

        public ContactSyncEntry(int capacity) {
            numbersToSync = new ArrayList<String>(capacity);
            nameToSync = new ArrayList<String>(capacity);
            emailToSync = new ArrayList<String>(capacity);
            this.capacity = capacity;
        }

        public boolean isSynced() {
            return mSynced;
        }

        public void setSynced(boolean mSynced) {
            this.mSynced = mSynced;
        }

        public boolean isFull() {
            return numbersToSync.size() == capacity;
        }

        public void addNumber(String number) {

            if (!TextUtils.isEmpty(number)) {
                numbersToSync.add(number);
            }
        }

        public void addName(String name) {

            if (!TextUtils.isEmpty(name)) {
                nameToSync.add(name);
            }
        }

        public void addEmail(String email) {

            if (!TextUtils.isEmpty(email)) {
                emailToSync.add(email);
            }
        }

        public void removeNumber(String number) {
            numbersToSync.remove(number);
        }

        public void removeName(String name) {
            nameToSync.remove(name);
        }


        public String getJoinedNumbersCsv() {

            final List<String> quotedNumbers = new ArrayList<String>(numbersToSync.size());

            for (String eachNumber : numbersToSync) {
                quotedNumbers.add(String.format(Locale.US, "'%s'", eachNumber));
            }


            return TextUtils.join(",", quotedNumbers);
        }

        public String getJoinedNamesCsv() {
            final List<String> quotedNames = new ArrayList<>(nameToSync.size());

            for (String eachName : nameToSync) {
                quotedNames.add(String.format(Locale.US, "'%s'", eachName));
            }
            return TextUtils.join(",", quotedNames);

        }

    }
}
