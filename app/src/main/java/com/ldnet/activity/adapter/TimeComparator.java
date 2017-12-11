package com.ldnet.activity.adapter;

import java.util.Comparator;

public class TimeComparator implements Comparator<TimeBean> {

	@Override
	public int compare(TimeBean lhs, TimeBean rhs) {
		return rhs.getCreated().compareTo(lhs.getCreated());
	}
}
