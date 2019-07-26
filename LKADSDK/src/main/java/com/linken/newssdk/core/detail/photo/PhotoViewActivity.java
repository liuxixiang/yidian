/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.linken.newssdk.core.detail.photo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.linken.newssdk.IntentConstants;
import com.linken.newssdk.R;
import com.linken.newssdk.libraries.photoview.PhotoView;
import com.linken.newssdk.utils.support.ImageLoaderHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangzhun
 */
public class PhotoViewActivity extends FragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.ydsdk_activity_view_pager);
		ViewPager viewPager = findViewById(R.id.view_pager);
		SamplePagerAdapter adapter = new SamplePagerAdapter();
		List<String> list = new ArrayList<>(1);
		String img = getIntent().getStringExtra(IntentConstants.IMG);
		if (!TextUtils.isEmpty(img)) {
			list.add(img);
		}
		adapter.setData(list);
		viewPager.setAdapter(adapter);
	}

	public static void starPhotoActivity(Context context, String img) {
		Intent intent = new Intent(context, PhotoViewActivity.class);
		intent.putExtra(IntentConstants.IMG, img);
		context.startActivity(intent);
	}


	static class SamplePagerAdapter extends PagerAdapter {

		private List<String> imgList = new ArrayList<>(1);


		public void setData(List<String> dataSource) {
			imgList.clear();
			imgList.addAll(dataSource);
		}

		@Override
		public int getCount() {
			return imgList.size();
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			PhotoView photoView = new PhotoView(container.getContext());
			ImageLoaderHelper.displayBigImage(photoView, imgList.get(position));
			container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			return photoView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}
}
