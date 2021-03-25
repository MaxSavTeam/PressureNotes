package com.maxsavteam.pressurenotes.utils;

import com.maxsavteam.pressurenotes.R;

public class ColorsLevelResolver {

	public static int getColorForSys(int val){
		if(val < 120)
			return R.color.light_green;
		else if(val < 130)
			return R.color.green;
		else if(val < 140)
			return R.color.yellow;
		else if(val < 160)
			return R.color.orange;
		else if(val < 180)
			return R.color.red;
		else
			return R.color.dark_red;
	}

	public static int getColorForDia(int val){
		if(val < 80)
			return R.color.light_green;
		else if(val < 85)
			return R.color.green;
		else if(val < 90)
			return R.color.yellow;
		else if(val < 100)
			return R.color.orange;
		else if(val < 110)
			return R.color.red;
		else
			return R.color.dark_red;
	}

}
