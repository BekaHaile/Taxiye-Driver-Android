package product.clicklabs.jugnoo.driver.fragments;

/**
 * Created by gurmail on 1/21/17.
 */

//public class RateFragment extends AbstractDodoFragment implements View.OnClickListener, VehiclesTabAdapter.Callback {

//    private static final String TAG = RateFragment.class.getSimpleName();
//    private HomeActivity homeActivity;
//    private View rootView;
//    private RecyclerView recyclerViewVehicles, recyclerView;
//    private RateCardAdapter rateCardAdapter;
//    private VehiclesTabAdapter vehiclesTabAdapter;
//    private CountrySpinnerAdapter adapter;
//    ArrayAdapter<String> dataAdapter;
//    private GetAllCitiesModel getAllCitiesModel;
//    private Spinner city_code_spinner;
//    private boolean clickFlag = false;
//    private static final Long ANIMATION_TRANSITION_TIME = 500L; // 500 MS
//
//    public int getPositionSelection() {
//        return positionSelection;
//    }
//
//    public void setPositionSelection(int positionSelection) {
//        this.positionSelection = positionSelection;
//    }
//
//    private int positionSelection = 0;
//
//    private LinearLayout delivertExtraLayout, mainLayout;
//    private LinearLayout basefare_extras, per_delivery_extras, perkm_extras;
//    private RelativeLayout uper_basefare_layout, uper_perkm_layout, uper_delivery_layout;
//    private RelativeLayout cityChangeLayout;
//    private TextView baseFareTextValue, preKmTextViewValue, deliveryTextViewValue, textViewBasetext, textViewBasetextValue,
//            baseText, perText, deliveryText, baseTitle, perTitel, deliveryTitle, cityName;
//    private ImageView basefare_arrow, deliveryArrow, perkm_arrow;
//
//    public PickupFareData getPickupFare() {
//        return pickupFare;
//    }
//
//    public void setPickupFare(PickupFareData pickupFare) {
//        this.pickupFare = pickupFare;
//    }
//
//    private PickupFareData pickupFare;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        init(container, savedInstanceState);
//        setHasOptionsMenu(false);
//        rootView = inflater.inflate(R.layout.fragment_rate, container, false);
//
//        homeActivity = (HomeActivity) getActivity();
//        homeActivity.updateToolBar(DodoApplication.getInstance().ACTIVITY_NAME_RATECARD);
//        init(rootView);
//
//        return rootView;
//    }
//
//    private void init(View parentView) {
//
//        delivertExtraLayout = (LinearLayout) parentView.findViewById(R.id.delivert_extra_layout);
//        mainLayout = (LinearLayout) parentView.findViewById(R.id.main_layout);
//        cityChangeLayout = (RelativeLayout) parentView.findViewById(R.id.city_change_layout);
//        uper_basefare_layout = (RelativeLayout) parentView.findViewById(R.id.uper_basefare_layout);
//        uper_perkm_layout = (RelativeLayout) parentView.findViewById(R.id.uper_perkm_layout);
//        uper_delivery_layout = (RelativeLayout) parentView.findViewById(R.id.uper_delivery_layout);
//
//        uper_basefare_layout.setOnClickListener(this);
//        uper_perkm_layout.setOnClickListener(this);
//        uper_delivery_layout.setOnClickListener(this);
//
//        basefare_extras = (LinearLayout) parentView.findViewById(R.id.basefare_extras);
//        basefare_extras.setVisibility(View.GONE);
//
//        per_delivery_extras = (LinearLayout) parentView.findViewById(R.id.per_delivery_extras);
//        per_delivery_extras.setVisibility(View.GONE);
//
//        perkm_extras = (LinearLayout) parentView.findViewById(R.id.perkm_extras);
//        perkm_extras.setVisibility(View.GONE);
//
//
//        cityChangeLayout.setOnClickListener(this);
//        mainLayout.setOnClickListener(this);
//
//        recyclerViewVehicles = (RecyclerView) parentView.findViewById(R.id.recyclerViewVehicles);
//        recyclerViewVehicles.setLayoutManager(new LinearLayoutManagerForResizableRecyclerView(homeActivity, LinearLayoutManager.HORIZONTAL, false));
//        recyclerViewVehicles.setItemAnimator(new DefaultItemAnimator());
//        recyclerViewVehicles.setHasFixedSize(false);
//
//        recyclerView = (RecyclerView) parentView.findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManagerForResizableRecyclerView(homeActivity, LinearLayoutManager.VERTICAL, false));
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setHasFixedSize(false);
//
//        rateCardAdapter = new RateCardAdapter(homeActivity, null, new RateCardAdapter.Callback() {
//            @Override
//            public void onSlotSelected(int position) {
//
//            }
//        });
//        recyclerView.setAdapter(rateCardAdapter);
//
//        baseTitle = (TextView) parentView.findViewById(R.id.baseTitle);
//        perTitel = (TextView) parentView.findViewById(R.id.perTitel);
//        deliveryTitle = (TextView) parentView.findViewById(R.id.deliveryTitle);
//
//        baseFareTextValue = (TextView) parentView.findViewById(R.id.base_fare_rate);
//        preKmTextViewValue = (TextView) parentView.findViewById(R.id.per_km_rate);
//        deliveryTextViewValue = (TextView) parentView.findViewById(R.id.delivery_rate);
//
//        baseText = (TextView) parentView.findViewById(R.id.base_text);
//        perText = (TextView) parentView.findViewById(R.id.per_text);
//        deliveryText = (TextView) parentView.findViewById(R.id.delivery_text);
//
//        textViewBasetext = (TextView) parentView.findViewById(R.id.textViewBasetext);
//        textViewBasetextValue = (TextView) parentView.findViewById(R.id.textViewBasetextValue);
//
//        cityName = (TextView) parentView.findViewById(R.id.city_name);
//
//        basefare_arrow = (ImageView) parentView.findViewById(R.id.basefare_arrow);
//
//        deliveryArrow = (ImageView) parentView.findViewById(R.id.diff_prices);
//
//        perkm_arrow = (ImageView) parentView.findViewById(R.id.perkm_arrow);
//
//        pickupFare = new PickupFareData();
//        pickupFare.setVehicle(homeActivity.getResponse().getData().getVehicle());
//        pickupFare.setFare(homeActivity.getResponse().getData().getFare());
//        pickupFare.setCityId(homeActivity.getResponse().getData().getCityId());
//        pickupFare.setCityName(homeActivity.getResponse().getData().getCityName());
//
//
//        //---------------------------------------------------------------------------------------
//        city_code_spinner = (Spinner) parentView.findViewById(R.id.spinner_city_code);
//        adapter = new CountrySpinnerAdapter(getContext(), null, getCountryCode());
//        city_code_spinner.setAdapter(adapter);
//        city_code_spinner.setDropDownVerticalOffset(Utils.dipToPixel(homeActivity, 40));
////        city_code_spinner.setPrompt(pickupFare.getCityName());
//        city_code_spinner.setGravity(Gravity.CENTER_HORIZONTAL);
//
//        city_code_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                try {
//                    if(clickFlag) {
//                        updatePickupCityFare(String.valueOf(getAllCitiesModel.getCity().get(position).getCityId()));
//                    } else {
//                        city_code_spinner.performClick();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // your code here
//            }
//
//        });
//
//        //---------------------------------------------------------------------------------------
//
//        setDate();
//        onVehicleSelected(0, false);
//    }
//
//    @Override
//    protected Object getTaskTag() {
//        return null;
//    }
//
//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        if(!hidden) {
//            homeActivity.updateToolBar(DodoApplication.getInstance().ACTIVITY_NAME_RATECARD);
//        }
//    }
//
//    private void setDate() {
//        try {
//            ArrayList<Vehicle> vehicle = (ArrayList<Vehicle>) getPickupFare().getVehicle();
//            ArrayList<Fare> fares = (ArrayList<Fare>) getPickupFare().getFare();
//            vehiclesTabAdapter = new VehiclesTabAdapter(true, homeActivity, vehicle, fares, this);
//            recyclerViewVehicles.setAdapter(vehiclesTabAdapter);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public void updatePickupCityFare(final String cityId) {
//        new ApiFetchCityFare(homeActivity, false, new ApiFetchCityFare.Callback() {
//
//            @Override
//            public void onSuccess(boolean flag, PickupFare pickupFare) {
//                setPickupFare(pickupFare.getData());
//                onVehicleSelected(0, false);
//                vehiclesTabAdapter.setData((ArrayList<Vehicle>) pickupFare.getData().getVehicle(), (ArrayList<Fare>) pickupFare.getData().getFare());
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//
//            @Override
//            public void onFailure() {
//
//            }
//
//            @Override
//            public void onRetry(View view) {
//                updatePickupCityFare(cityId);
//            }
//
//            @Override
//            public void onNoRetry(View view) {
//                updatePickupCityFare(cityId);
//            }
//        }).getCityFare(true, null, "" + cityId);
//    }
//
//
//    void setTextValue(Fare fare) {
//
//        textViewBasetext.setText("" + fare.getBaseTextTitle());
//        textViewBasetextValue.setText("" + fare.getBaseTextValue());
//
//        if (!TextUtils.isEmpty(fare.getBaseTextTitle()) && !TextUtils.isEmpty(fare.getBaseTextValue())) {
//            delivertExtraLayout.setVisibility(View.VISIBLE);
//        } else {
//            delivertExtraLayout.setVisibility(View.GONE);
//        }
//
//        cityName.setText("" + getPickupFare().getCityName());
//
////        rateCardAdapter.setList((ArrayList<Fare.FareDisplayText>) fare.getFareDisplayText());
//
//        baseTitle.setText("" + fare.getFareDisplayText().get(0).getTop());
//        perTitel.setText("" + fare.getFareDisplayText().get(1).getTop());
//        deliveryTitle.setText("" + fare.getFareDisplayText().get(2).getTop());
//
//        baseText.setText("" + fare.getFareDisplayText().get(0).getMiddle());
//        perText.setText("" + fare.getFareDisplayText().get(1).getMiddle());
//        deliveryText.setText("" + fare.getFareDisplayText().get(2).getMiddle());
//
//        baseFareTextValue.setText("" + fare.getFareDisplayText().get(0).getBottom());
//        preKmTextViewValue.setText("" + fare.getFareDisplayText().get(1).getBottom());
//        deliveryTextViewValue.setText("" + fare.getFareDisplayText().get(2).getBottom());
//
//        if (!TextUtils.isEmpty(fare.getBaseTextTitle()) && !TextUtils.isEmpty(fare.getBaseTextValue())) {
//            delivertExtraLayout.setVisibility(View.VISIBLE);
//        } else {
//            delivertExtraLayout.setVisibility(View.GONE);
//        }
//
//        cityName.setText("" + getPickupFare().getCityName());
//
//        basefare_extras.removeAllViews();
//        per_delivery_extras.removeAllViews();
//        perkm_extras.removeAllViews();
//
//        if(fare.getFareDisplayText() != null && fare.getFareDisplayText().get(0).getDisplayText() != null && fare.getFareDisplayText().get(0).getDisplayText().size()>1) {
//            for(int i=0;i<fare.getFareDisplayText().get(0).getDisplayText().size();i++) {
//                if(i==0) {
//                    basefare_extras.addView(getAdditionalData(true, fare.getFareDisplayText().get(0).getDisplayText().get(i).getLeft(),
//                            fare.getFareDisplayText().get(0).getDisplayText().get(i).getRight()));
//                } else {
//                    basefare_extras.addView(getAdditionalData(false, fare.getFareDisplayText().get(0).getDisplayText().get(i).getLeft(),
//                            fare.getFareDisplayText().get(0).getDisplayText().get(i).getRight()));
//                }
//            }
//            basefare_arrow.setVisibility(View.VISIBLE);
//            uper_basefare_layout.setEnabled(true);
//        } else {
//            basefare_arrow.animate().rotation(0f);
//            basefare_arrow.setVisibility(View.INVISIBLE);
//            basefare_extras.setVisibility(View.GONE);
//            uper_basefare_layout.setEnabled(false);
//        }
//
//        if(fare.getFareDisplayText() != null && fare.getFareDisplayText().get(1).getDisplayText() != null && fare.getFareDisplayText().get(1).getDisplayText().size()>1) {
//            for(int i=0;i<fare.getFareDisplayText().get(1).getDisplayText().size();i++) {
//                if(i==0) {
//                    perkm_extras.addView(getAdditionalData(true, fare.getFareDisplayText().get(1).getDisplayText().get(i).getLeft(),
//                            fare.getFareDisplayText().get(1).getDisplayText().get(i).getRight()));
//                } else {
//                    perkm_extras.addView(getAdditionalData(false, fare.getFareDisplayText().get(1).getDisplayText().get(i).getLeft(),
//                            fare.getFareDisplayText().get(1).getDisplayText().get(i).getRight()));
//                }
//            }
//            perkm_arrow.setVisibility(View.VISIBLE);
//            uper_perkm_layout.setEnabled(true);
//        } else {
//            perkm_arrow.animate().rotation(0f);
//            perkm_arrow.setVisibility(View.INVISIBLE);
//            perkm_extras.setVisibility(View.GONE);
//            uper_perkm_layout.setEnabled(false);
//        }
//
//        if(fare.getFareDisplayText() != null && fare.getFareDisplayText().get(2).getDisplayText() != null && fare.getFareDisplayText().get(2).getDisplayText().size()>1) {
//            for(int i=0;i<fare.getFareDisplayText().get(2).getDisplayText().size();i++) {
//                if(i==0) {
//                    per_delivery_extras.addView(getAdditionalData(true, fare.getFareDisplayText().get(2).getDisplayText().get(i).getLeft(),
//                            fare.getFareDisplayText().get(2).getDisplayText().get(i).getRight()));
//                } else {
//                    per_delivery_extras.addView(getAdditionalData(false, fare.getFareDisplayText().get(2).getDisplayText().get(i).getLeft(),
//                            fare.getFareDisplayText().get(2).getDisplayText().get(i).getRight()));
//                }
//            }
//            uper_delivery_layout.setEnabled(true);
//            deliveryArrow.setVisibility(View.VISIBLE);
//        } else {
//            deliveryArrow.animate().rotation(0f);
//            deliveryArrow.setVisibility(View.INVISIBLE);
//            per_delivery_extras.setVisibility(View.GONE);
//            uper_delivery_layout.setEnabled(false);
//        }
//
//
//    }
//
//    @Override
//    public void onVehicleSelected(int position, boolean supplyType) {
//        setPositionSelection(position);
//        Integer vahicleType = pickupFare.getVehicle().get(position).getVehicleType();
//        for (int i = 0; i < pickupFare.getFare().size(); i++) {
//            if (vahicleType.equals(pickupFare.getFare().get(i).getVehicleType())) {
//                setTextValue(pickupFare.getFare().get(i));
//                break;
//            }
//        }
//    }
//
//    @Override
//    public void onReClicked() {
//
//    }
//
//    /**
//     * Method used to get All Active cities for sign up
//     */
//    private void getSignUpInfo() {
//        try {
//            if (homeActivity.isNetworkAvailable()) {
//                DialogPopup.showLoadingDialog(homeActivity, "Loading...");
//
//                RestClient.getApiServices().signUpInfo("", new Callback<GetAllCitiesModel>() {
//                    @Override
//                    public void success(GetAllCitiesModel getAllCities, Response response) {
//                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
//                        Logger.i(TAG, "cities name response = " + responseStr);
//                        try {
//                            JSONObject jObj = new JSONObject(responseStr);
//                            String message = JSONParser.getServerMessage(jObj);
//                            int flag = jObj.getInt(Data.STATUS);
//                            if (ApiResponseFlags.SUCCESS_COMPLETE.getOrdinal() == flag) {
//                                getAllCitiesModel = getAllCities;
//                                clickFlag = false;
//                                popupWindowsort(getAllCitiesModel);
//                            } else {
//                                DialogPopup.alertPopup(homeActivity, "", message);
//                            }
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            DialogPopup.alertPopup(homeActivity, "", Data.SERVER_ERROR_MSG);
//                        }
//                        DialogPopup.dismissLoadingDialog();
//                    }
//
//                    @Override
//                    public void failure(RetrofitError error) {
//                        Logger.e(TAG, "error=" + error.toString());
//                        DialogPopup.dismissLoadingDialog();
//                        DialogPopup.alertPopup(homeActivity, "", Data.SERVER_ERROR_MSG);
//                    }
//                });
//            } else {
//                new MaterialDialog.Builder(homeActivity)
//                        .title(Data.CHECK_INTERNET_TITLE)
//                        .content(Data.CHECK_INTERNET_MSG)
//                        .positiveText("OK")
//                        .positiveColor(getResources().getColor(R.color.colorPrimary))
//                        .negativeColor(getResources().getColor(R.color.colorPrimary))
//                        .callback(new MaterialDialog.ButtonCallback() {
//                            @Override
//                            public void onPositive(MaterialDialog dialog) {
//                                super.onPositive(dialog);
//                                getSignUpInfo();
//                                dialog.dismiss();
//                            }
//                        })
//                        .show();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//        switch (id) {
//            case R.id.city_change_layout:
//                if (getAllCitiesModel == null) {
//                    getSignUpInfo();
//                } else {
//                    clickFlag = true;
//                    city_code_spinner.performClick();
//                }
//                break;
//            case R.id.uper_basefare_layout:
//                if(basefare_extras.getVisibility() == View.GONE) {
//                    basefare_arrow.animate().rotation(90f);
//                    showElements(basefare_extras);
//                } else {
//                    basefare_arrow.animate().rotation(0f);
//                    hideElements(basefare_extras);
//                }
//                break;
//            case R.id.uper_perkm_layout:
//                if(perkm_extras.getVisibility() == View.GONE) {
//                    perkm_arrow.animate().rotation(90f);
//                    showElements(perkm_extras);
//                } else {
//                    perkm_arrow.animate().rotation(0f);
//                    hideElements(perkm_extras);
//                }
//                break;
//            case R.id.uper_delivery_layout:
//                if(per_delivery_extras.getVisibility() == View.GONE) {
//                    deliveryArrow.animate().rotation(90f);
//                    showElements(per_delivery_extras);
//                } else {
//                    deliveryArrow.animate().rotation(0f);
//                    hideElements(per_delivery_extras);
//                }
//                break;
//        }
//    }
//
//    /**
//     * show popup window method reuturn PopupWindow
//     *
//     * @param getAllCitiesModel
//     */
//    int index = 0;
//    private void popupWindowsort(GetAllCitiesModel getAllCitiesModel) {
//
//        for (int i = 0; i < this.getAllCitiesModel.getCity().size(); i++) {
//            if(pickupFare.getCityId().equals(getAllCitiesModel.getCity().get(i).getCityId())) {
//                index = i;
//                break;
//            }
//        }
//        adapter.setData((ArrayList<GetAllCitiesModel.City>) getAllCitiesModel.getCity());
//        city_code_spinner.setSelection(index, true);
//        city_code_spinner.performClick();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                clickFlag = true;
//            }
//        }, 300);
//    }
//
//
//    private View getAdditionalData(boolean first, String left, String right) {
//        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = layoutInflater.inflate(R.layout.item_additional_fare_view, null, false);
//        LinearLayout layout = (LinearLayout) view.findViewById(R.id.main_layout);
//        TextView leftText = (TextView) view.findViewById(R.id.left_text_view);
//        TextView rightText = (TextView) view.findViewById(R.id.right_text_view);
//        View topLine = view.findViewById(R.id.top_line);
//        topLine.setVisibility(View.GONE);
//        if(first) {
//            topLine.setVisibility(View.VISIBLE);
//            leftText.setTextColor(homeActivity.getResources().getColor(R.color.colorPrimary));
//            rightText.setTextColor(homeActivity.getResources().getColor(R.color.colorPrimary));
//            leftText.setBackgroundResource(R.color.colorPrimary20);
//            rightText.setBackgroundResource(R.color.colorPrimary20);
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(60));
//            layout.setLayoutParams(layoutParams);
//        }
//        leftText.setText(left);
//        rightText.setText(right);
//        return view;
//    }
//
//
//    private void hideElements(final View additionalView) {
//
//        // Animate the hidden linear layout as visible and set
//        additionalView
//                .animate()
//                .setDuration(ANIMATION_TRANSITION_TIME)
//                .alpha(0.0f)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        Logger.v(TAG, "Animation ended. Set the view as gone");
//                        super.onAnimationEnd(animation);
//                        additionalView.setVisibility(View.GONE);
//                        // Hack: Remove the listener. So it won't be executed when
//                        // any other animation on this view is executed
//                        additionalView.animate().setListener(null);
//                    }
//                });
//
//    }
//
//    private void showElements(View additionalView) {
//
//        // Animate the hidden linear layout as visible and set
//        // the alpha as 0.0. Otherwise the animation won't be shown
//        additionalView.setVisibility(View.VISIBLE);
//        additionalView.setAlpha(0.0f);
//        additionalView
//                .animate()
//                .setDuration(ANIMATION_TRANSITION_TIME)
//                .alpha(1.0f)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        super.onAnimationEnd(animation);
//                    }
//                });
//
//    }
//}
