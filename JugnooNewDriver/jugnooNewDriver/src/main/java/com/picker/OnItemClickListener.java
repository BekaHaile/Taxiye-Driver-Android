package com.picker;

import product.clicklabs.jugnoo.driver.ui.models.SearchDataModel;

public interface OnItemClickListener<T extends SearchDataModel> {
  void onItemClicked(T country);
}