package com.picker;

import product.clicklabs.jugnoo.driver.ui.models.SearchDataModel;

public interface OnCountryPickerListener<T extends SearchDataModel> {
  void onSelectCountry(T country);
}
