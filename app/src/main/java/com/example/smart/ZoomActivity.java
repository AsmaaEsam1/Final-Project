package com.example.smart;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import us.zoom.sdk.AccountService;
import us.zoom.sdk.Alternativehost;
import us.zoom.sdk.InviteOptions;
import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingItem;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingViewsOptions;
import us.zoom.sdk.MobileRTCDialinCountry;
import us.zoom.sdk.PreMeetingService;
import us.zoom.sdk.PreMeetingServiceListener;
import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitializeListener;

public class ZoomActivity extends AppCompatActivity implements View.OnClickListener, PreMeetingServiceListener {
    private final static String TAG = "ZoomSDKExample";
    private ListView mListView;
    private Button mBtnSchedule;
    String meetingNumber;
    private MeetingsListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);

        mListView = (ListView) findViewById(R.id.meetingsListView);
        mBtnSchedule = (Button) findViewById(R.id.btnSchedule);
        mBtnSchedule.setOnClickListener(this);
        mAdapter = new MeetingsListAdapter(this);
        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        if (zoomSDK.isInitialized()) {
            PreMeetingService preMeetingService = zoomSDK.getPreMeetingService();
            if (preMeetingService != null) {
                preMeetingService.listMeeting();
                preMeetingService.addListener(this);
            } else {
                Toast.makeText(this, "User not login.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        mListView.setAdapter(mAdapter);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onListMeeting(int result, List<Long> meetingList) {
        Log.i(TAG, "onListMeeting, result =" + result);
        mAdapter.clear();
        ZoomSDK zoomSDK = ZoomSDK.getInstance();
        PreMeetingService preMeetingService = zoomSDK.getPreMeetingService();
        if (preMeetingService != null) {
            if (meetingList != null) {
                for (long meetingUniqueId : meetingList) {
                    MeetingItem item = preMeetingService.getMeetingItemByUniqueId(meetingUniqueId);
                    if (item != null) {
                        mAdapter.addItem(item);
                    }
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onScheduleMeeting(int result, long meetingUniqueId) {

    }

    @Override
    public void onUpdateMeeting(int result, long meetingUniqueId) {

    }

    @Override
    public void onDeleteMeeting(int result) {

    }
    private void onClickBtnDelete(MeetingItem item) {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if(zoomSDK.isInitialized()) {
            PreMeetingService preMeetingService = zoomSDK.getPreMeetingService();
            if(preMeetingService != null) {
                preMeetingService.deleteMeeting(item.getMeetingUniqueId());
            }
        }
    }
    @Override
    protected void onDestroy() {
        ZoomSDK zoomSDK = ZoomSDK.getInstance();

        if(zoomSDK.isInitialized()) {
            PreMeetingService preMeetingService = zoomSDK.getPreMeetingService();
            if(preMeetingService != null)
                preMeetingService.removeListener(this);
        }

        super.onDestroy();
    }

    class MeetingsListAdapter extends BaseAdapter {
        private ArrayList<MeetingItem> mItems = new ArrayList<MeetingItem>();
        private Context mContext;

        public MeetingsListAdapter(Context context) {
            mContext = context;
        }

        public void clear() {
            mItems.clear();
        }

        public void addItem(MeetingItem item) {
            assert (item != null);
            mItems.add(item);
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            if (position < 0 || position >= getCount())
                return null;

            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            MeetingItem item = (MeetingItem) getItem(position);
            return item != null ? item.getMeetingUniqueId() : 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final MeetingItem item = (MeetingItem) getItem(position);
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.meeting_item, null);
                holder.txtTopic = convertView.findViewById(R.id.txtTopic);
                holder.txtTime = convertView.findViewById(R.id.txtTime);
                holder.txtHostName = convertView.findViewById(R.id.txtHostName);
                holder.txtMeetingNo = convertView.findViewById(R.id.txtMeetingNo);
                holder.btnDelete = convertView.findViewById(R.id.btnDelete);
                holder.btnStart = convertView.findViewById(R.id.btnStart);
                holder.txtMeeting2 = convertView.findViewById(R.id.txtMeeting2);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txtMeetingNo.setText("Meeting number: " + item.getMeetingNumber());
            meetingNumber = holder.txtMeeting2.getText().toString().trim();
            holder.txtMeeting2.setText(meetingNumber);

            if (item.isPersonalMeeting()) {
                holder.btnDelete.setVisibility(View.GONE);
                holder.txtTopic.setText("Personal meeting id(PMI)");
                holder.txtHostName.setVisibility(View.GONE);
                holder.txtTime.setVisibility(View.GONE);
            } else {
                holder.txtTopic.setText("Topic: " + item.getMeetingTopic());
                holder.txtHostName.setText(getHostNameByEmail(item.getScheduleForHostEmail()));
                holder.txtMeeting2.setText("Meeting: " + item.getMeetingNumber());
                Date date = new Date(item.getStartTime());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
                holder.txtTime.setText("Time: " + sdf.format(date));
                holder.btnStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ZoomSDK zoomSDK = ZoomSDK.getInstance();
                        if (zoomSDK.isInitialized()) {
                            String meetingN = String.valueOf(item.getMeetingNumber());
                            Toast.makeText(ZoomActivity.this, "starting meeting" + meetingN, Toast.LENGTH_LONG).show();
                            // Check if the meeting number is empty.
                            if (meetingN.length() == 0) {
                                Toast.makeText(ZoomActivity.this, "You need to enter a meeting number/ vanity id which you want to join.", Toast.LENGTH_LONG).show();
                                return;
                            }
                            // Step 2: Get Zoom SDK instance.
                            // Check if the zoom SDK is initialized
                            if (!zoomSDK.isInitialized()) {
                                Toast.makeText(ZoomActivity.this, "ZoomSDK has not been initialized successfully", Toast.LENGTH_LONG).show();
                                return;
                            }
                            // Step 3: Get meeting service from zoom SDK instance.
                            MeetingService meetingService = zoomSDK.getMeetingService();
                            // Step 4: Configure meeting options.
                            JoinMeetingOptions opts = new JoinMeetingOptions();
                            // Some available options
                            opts.no_driving_mode = false;
                            opts.no_invite = false;
                            opts.no_meeting_end_message = false;
                            opts.no_titlebar = false;
                            opts.no_bottom_toolbar = false;
                            opts.no_dial_in_via_phone = false;
                            opts.no_dial_out_to_phone = false;
                            opts.no_disconnect_audio = false;
                            opts.no_share = true;
                            opts.invite_options = InviteOptions.INVITE_VIA_EMAIL + InviteOptions.INVITE_VIA_SMS;
                            opts.no_audio = false;
                            opts.no_video = false;
                            opts.meeting_views_options = MeetingViewsOptions.NO_BUTTON_SHARE;
                            opts.no_meeting_error_message = false;
                            opts.participant_id = "participant id";
                            // Step 5: Setup join meeting parameters
                            JoinMeetingParams params = new JoinMeetingParams();
                            // params.displayName = "Hello World From Zoom SDK";
                            params.meetingNo = meetingN;
                            // Step 6: Call meeting service to join meeting
                            meetingService.joinMeetingWithParams(ZoomActivity.this, params, opts);
                        }
                    }
                });
                if (item.isWebinarMeeting()) {
                    holder.btnDelete.setVisibility(View.GONE);
                } else {
                    holder.btnDelete.setVisibility(View.VISIBLE);
                    holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            onClickBtnDelete(item);

                        }
                    });
                }
            }
            return convertView;
        }

        class ViewHolder {
            public TextView txtTopic;
            public TextView txtHostName;
            public TextView txtTime;
            public TextView txtMeetingNo;
            public TextView txtMeeting2;
            public Button btnDelete;
            public Button btnStart;
        }

    }
    @Override
    public void onClick(View arg0) {
        if(arg0.getId() == R.id.btnSchedule) {
            onClickSchedule();
        }
    }

    private void onClickSchedule() {
        Intent intent = new Intent(ZoomActivity.this, ScheduleMeeting.class);
        startActivity(intent);
    }

    private String getHostNameByEmail(String email) {
        AccountService accountService = ZoomSDK.getInstance().getAccountService();
        if(accountService != null) {
            if(email.equals(accountService.getAccountEmail())) {
                return accountService.getAccountName();
            }

            List<Alternativehost> hostList = accountService.getCanScheduleForUsersList();

            if(hostList.size() < 1) return " ";

            for(Alternativehost host : hostList) {
                if(email.equals(host.getEmail())) {
                    return host.getFirstName() + " "+ host.getLastName();
                }
            }
        }
        return " ";
    }

}

