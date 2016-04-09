package com.jhone.demo.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 
 * @author jhone
 * 
 * @param <T> Bean类型
 * 适配类只需继承这个类并实现initView方法
 */
public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {

	protected Context mContext;
	protected List<T> mDatas;
	protected LayoutInflater mInflater;
	private int layoutId;

	public BaseAdapter(Context context, List<T> datas, int layoutId) {
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		this.mDatas = datas;
		this.layoutId = layoutId;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public T getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = ViewHolder.get(mContext, convertView, parent,
				layoutId, position);
		initView(holder, getItem(position));
		return holder.getConvertView();
	}

	/**
	 * 
	 * @param holder 通过holder.get方法获取控件并初始化
	 * @param t bean对象
	 */
	public abstract void initView(ViewHolder holder, T t);

}
