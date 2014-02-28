/*
	Copyright 2013-2014, JUMA Technology

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/

package org.bcsphere.bluetooth.tools;



import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class BluetoothDetection {
	private static SharedPreferences sp;
	private static Editor editor;
	public static final void detectionBluetoothAPI(final Context context)
	{
		 sp  = context.getSharedPreferences("VERSION_OF_API", 1);
		 editor = sp.edit();
		if (Tools.isSupportUniversalAPI()) {
			editor.putString("API", "google").commit();
		}else {
			if (Tools.isSupportSpecificAPI("samsung")) {
				editor.putString("API", "samsung").commit();
			}else if (Tools.isSupportSpecificAPI("htc")) {
				
			}else if (Tools.isSupportSpecificAPI("xiaomi")){
				
			}else if (Tools.isSupportSpecificAPI("motorola")) {
				
			}else {
				if (Tools.getSupportBasebandVersionBrand() != null) {
					if (Tools.getSupportBasebandVersionBrand().equals("xiaomi")) {
						
					}
				}else {
					editor.putString("API", "").commit();
				}
			}
		}
	}

	
	
	
}
