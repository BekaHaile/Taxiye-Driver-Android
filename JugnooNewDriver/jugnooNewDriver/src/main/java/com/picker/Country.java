package com.picker;

import android.content.Context;
import android.text.TextUtils;

import java.util.Locale;

import product.clicklabs.jugnoo.driver.ui.models.SearchDataModel;

public class Country extends SearchDataModel{

  // region Variables
  private String code;
  private String name;
  private String dialCode;
  private int flag;
  private String currency;
  // endregion

  // region Constructors
  Country() {
  }

  Country(String code, String name, String dialCode, int flag, String currency) {
    this.code = code;
    this.name = name;
    this.dialCode = dialCode;
    this.flag = flag;
    this.currency = currency;
  }
  // endregion

  // region Getter/Setter
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
    if (TextUtils.isEmpty(name)) {
      name = new Locale("", code).getDisplayName();
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDialCode() {
    return dialCode;
  }

  public void setDialCode(String dialCode) {
    this.dialCode = dialCode;
  }

  public int getFlag() {
    return flag;
  }

  public void setFlag(int flag) {
    this.flag = flag;
  }

  public int loadFlagByCode(Context context) {
    if (this.flag != -1) {
      return this.flag;
    }

    try {
      this.flag = context.getResources()
          .getIdentifier("flag_" + this.code.toLowerCase(Locale.ENGLISH), "drawable",
              context.getPackageName());
    } catch (Exception e) {
      e.printStackTrace();
      this.flag = -1;
    }
    return this.flag;
  }

  @Override
  public String getLabel() {
    return name;
  }

  @Override
  public int getImage(Context context) {
    return loadFlagByCode(context);
  }
}
