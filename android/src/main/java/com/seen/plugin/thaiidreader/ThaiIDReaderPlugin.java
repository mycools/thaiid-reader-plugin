package com.seen.plugin.thaiidreader;

import android.util.Log;
import android.widget.Toast;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.util.ArrayList;

@CapacitorPlugin(name = "ThaiIDReader")
public class ThaiIDReaderPlugin extends Plugin {

    private ThaiIDReader implementation = new ThaiIDReader();

    @PluginMethod
    public void reader(PluginCall call) {
        String value = call.getString("value");

        SmartCardDevice device = SmartCardDevice.getSmartCardDevice(
                getActivity().getApplicationContext(),
                value,
                new SmartCardDevice.SmartCardDeviceEvent() {
                    @Override
                    public void OnReady(SmartCardDevice device) {
                        ThaiSmartCard thaiSmartCard = new ThaiSmartCard(device);

                        ThaiSmartCard.PersonalInformation info = thaiSmartCard.getPersonalInformation();

                        if (info == null) {
                            Toast.makeText(getActivity().getApplicationContext(), "Read Smart Card information failed", Toast.LENGTH_LONG)
                                    .show();
                            return;
                        }

//                    Bitmap personalPic = thaiSmartCard.getPersonalPicture();

                        String personalPic = thaiSmartCard.getPersonalPicture();
                        Log.d("personalPic", personalPic);
                        if (personalPic == null) {
                            Toast
                                    .makeText(getActivity().getApplicationContext(), "Read Smart Card personal picture failed", Toast.LENGTH_LONG)
                                    .show();
                            return;
                        }

                        String str = info.Address;
                        String[] arrOfStr = str.split("#");
                        ArrayList<String> address = new ArrayList<String>();

                        for (String a : arrOfStr){
                            Log.d("address:", a);

                            address.add(a);
                        }

                        Log.d("arrAddress", address.get(1));

                        JSObject ret = new JSObject();
                        ret.put("version", implementation.reader(info.Version));
                        ret.put("personalID", implementation.reader(info.PersonalID));
                        ret.put("nameTH", implementation.reader(info.NameTH));
                        ret.put("nameEN", implementation.reader(info.NameEN));
                        ret.put("birthDate", implementation.reader(info.BirthDate));
                        ret.put("gender", implementation.reader(info.Gender));
                        ret.put("requestNo", implementation.reader(info.RequestNo));
                        ret.put("issuer", implementation.reader(info.Issuer));
                        ret.put("issuerCode", implementation.reader(info.IssuerCode));
                        ret.put("issueDate", implementation.reader(info.IssueDate));
                        ret.put("expireDate", implementation.reader(info.ExpireDate));
                        ret.put("typeCard", implementation.reader(info.TypeCard));

                        ret.put("address_HouseNo", implementation.reader(address.get(0)));
                        ret.put("address_MooNo", implementation.reader(address.get(1)));
                        ret.put("address_Alley", implementation.reader(address.get(2)));
                        ret.put("address_Lane", implementation.reader(address.get(3)));
                        ret.put("address_Road", implementation.reader(address.get(4)));
                        ret.put("address_SubDistrict", implementation.reader(address.get(5)));
                        ret.put("address_District", implementation.reader(address.get(6)));
                        ret.put("address_Province", implementation.reader(address.get(7)));

                        ret.put("pictureTag", implementation.reader(info.PictureTag));
                        ret.put("personalPic", implementation.reader(personalPic));


                        call.resolve(ret);
                    }

                    @Override
                    public void OnDetached(SmartCardDevice device) {
                        Toast.makeText(getActivity().getApplicationContext(), "Smart Card is removed", Toast.LENGTH_LONG).show();
                    }
                }
        );

        if (device == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Smart Card device not found", Toast.LENGTH_LONG).show();
        }
    }
}
