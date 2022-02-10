package com.seen.plugin.thaiidreader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.ftsafe.DK;
import com.ftsafe.readerScheme.FTException;
import com.ftsafe.readerScheme.FTReader;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.util.ArrayList;

@CapacitorPlugin(name = "ThaiIDReader")
public class ThaiIDReaderPlugin extends Plugin {
    FTReader mFtReader;
    String ReaderName;
    int slotIndex = 0;
    String mHandlerMessage;
    private ThaiIDReader implementation = new ThaiIDReader();

    @PluginMethod
    public void findReader(PluginCall call) {
        //Set USB
        mFtReader = new FTReader(getActivity().getApplicationContext(), mHandler, DK.FTREADER_TYPE_USB);
            try {
                mFtReader.readerFind();
                JSObject find = new JSObject();
                find.put("status", implementation.findReader("success"));
                find.put("message", implementation.findReader("USB device connection Success."));
                find.put("mHandlerMessage", implementation.findReader(mHandlerMessage));
                call.resolve(find);
//                Toast.makeText(getActivity().getApplicationContext(), "success", Toast.LENGTH_LONG).show();
                //open in handler case usb_in.
            } catch (FTException e) {
                e.printStackTrace();
                JSObject find = new JSObject();
                find.put("status", implementation.findReader("failed"));
                find.put("message", implementation.findReader("USB device connection Failed."));
                find.put("mHandlerMessage", implementation.findReader(mHandlerMessage));
                call.resolve(find);
//                Toast.makeText(getActivity().getApplicationContext(), "connection failed.", Toast.LENGTH_LONG).show();
            }
    }

    @PluginMethod
    public void powerOn(PluginCall call) {
        try {
            byte[] atr = mFtReader.readerPowerOn(slotIndex);
            JSObject power = new JSObject();
            power.put("status", implementation.powerOn("success"));
            power.put("message", implementation.powerOn("Power on success."));
            call.resolve(power);
//            Toast.makeText(getActivity().getApplicationContext(), "Power on success. Atr : " + Convection.Bytes2HexString(atr), Toast.LENGTH_SHORT).show();
        } catch (FTException e) {
            e.printStackTrace();
            JSObject power = new JSObject();
            power.put("status", implementation.powerOn("failed"));
            power.put("message", implementation.powerOn("Power on failed."));
            call.resolve(power);
//            Toast.makeText(getActivity().getApplicationContext(), "Power on failed.\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @PluginMethod
    public void getNameReader(PluginCall call) {
        try {
            byte[] readername = mFtReader.readerGetReaderName();
            ReaderName = new String(readername);
            JSObject ret = new JSObject();
            ret.put("status", implementation.getNameReader("success"));
            ret.put("nameReader", implementation.getNameReader(new String(readername)));
            call.resolve(ret);
//            Toast.makeText(getActivity().getApplicationContext(), "Reader name : " + new String(readername), Toast.LENGTH_SHORT).show();
        } catch (FTException e) {
            e.printStackTrace();
            JSObject ret = new JSObject();
            ret.put("status", implementation.getNameReader("failed"));
            ret.put("message", implementation.getNameReader("Get reader name failed.\n" + e.getMessage()));
            call.resolve(ret);
//            Toast.makeText(getActivity().getApplicationContext(), "Get reader name failed.\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @PluginMethod
    public void reader(PluginCall call) {
        SmartCardDevice device = SmartCardDevice.getSmartCardDevice(
                getActivity().getApplicationContext(),
                ReaderName,
                new SmartCardDevice.SmartCardDeviceEvent() {
                    @Override
                    public void OnReady(SmartCardDevice device) {
                        ThaiSmartCard thaiSmartCard = new ThaiSmartCard(device);

                        ThaiSmartCard.PersonalInformation info = thaiSmartCard.getPersonalInformation();

                        if (info == null) {
                            call.errorCallback("Read Smart Card information failed");
//                            Toast.makeText(getActivity().getApplicationContext(), "Read Smart Card information failed", Toast.LENGTH_LONG)
//                                    .show();
                            return;
                        }

                        String personalPic = thaiSmartCard.getPersonalPicture();
                        Log.d("personalPic", personalPic);
                        if (personalPic == null) {
                            call.errorCallback("Read Smart Card personal picture failed");
//                            Toast
//                                    .makeText(getActivity().getApplicationContext(), "Read Smart Card personal picture failed", Toast.LENGTH_LONG)
//                                    .show();
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
                        ret.put("id_number", implementation.reader(info.PersonalID));
                        ret.put("th_name", implementation.reader(info.NameTH));
                        ret.put("en_name", implementation.reader(info.NameEN));
                        ret.put("dob", implementation.reader(info.BirthDate));
                        ret.put("gender", implementation.reader(info.Gender));
                        ret.put("request_no", implementation.reader(info.RequestNo));
                        ret.put("issuer", implementation.reader(info.Issuer));
                        ret.put("issuer_code", implementation.reader(info.IssuerCode));
                        ret.put("issue_date", implementation.reader(info.IssueDate));
                        ret.put("expire_date", implementation.reader(info.ExpireDate));
                        ret.put("type_card", implementation.reader(info.TypeCard));

                        ret.put("address_number", implementation.reader(address.get(0)));
                        ret.put("address_village_no", implementation.reader(address.get(1)));
                        ret.put("address_alley", implementation.reader(address.get(2)));
                        ret.put("address_lane", implementation.reader(address.get(3)));
                        ret.put("address_road", implementation.reader(address.get(4)));
                        ret.put("address_sub_district", implementation.reader(address.get(5)));
                        ret.put("address_district", implementation.reader(address.get(6)));
                        ret.put("address_province", implementation.reader(address.get(7)));

                        ret.put("pic_tag_no", implementation.reader(info.PictureTag));
                        ret.put("pic_face", implementation.reader(personalPic));


                        call.resolve(ret);
                    }

                    @Override
                    public void OnDetached(SmartCardDevice device) {
                        call.errorCallback("Smart Card is removed");
//                        Toast.makeText(getActivity().getApplicationContext(), "Smart Card is removed", Toast.LENGTH_LONG).show();
                    }
                }
        );

        if (device == null) {
            call.errorCallback("Smart Card device not found");
//            Toast.makeText(getActivity().getApplicationContext(), "Smart Card device not found", Toast.LENGTH_LONG).show();
        }
    }

    //Close device.
    @PluginMethod
    public void close(PluginCall call) {
        try {
            mFtReader.readerClose();
            JSObject ret = new JSObject();
            ret.put("status", implementation.close("success"));
            ret.put("message", implementation.close("USB device has been disconnected."));
            call.resolve(ret);
        }catch (FTException e) {
            e.printStackTrace();
        }
    }

    //Used to recieve reader events.
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String text = "";
            if (msg.obj instanceof String) {
                text = (String) msg.obj;
            }
            Log.e("cardreader", "handler msg. what : " + msg.what + ", obj : " + text);
            switch (msg.what) {
                //Bluetooth device scanned.
                case DK.BT4_NEW:
                case DK.BT3_NEW:
                    BluetoothDevice device3 = (BluetoothDevice) msg.obj;
                    if (device3 != null && device3.getName() != null) {
//                        if(deviceDialog != null) {
//                            deviceDialog.setData(device3);
//                        }

//                        Toast.makeText(MainActivity.this, device3.toString(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case DK.USB_LOG:
                    //Otg device found.
                    if (((String) msg.obj).contains("USB_PERMISSION")) {
                        try {
                            String[] names = mFtReader.readerOpen(null);
//                            if (names.length != 0) {
//                                addSpinnerPrivate(names);
//                            }
                            if (names != null) {
                                try {
                                    mFtReader.readerOpen(names);
                                } catch (FTException e) {
                                    e.printStackTrace();
                                }
                            }
                            mHandlerMessage = "USB device has been connected.";
//                            Toast.makeText(getActivity().getApplicationContext(), "USB device has been connected.", Toast.LENGTH_SHORT).show();
                        } catch (FTException e) {
                            e.printStackTrace();
                            mHandlerMessage = "USB device connection failed.";
//                            Toast.makeText(MainActivity.this, "USB device connection failed.", Toast.LENGTH_SHORT).show();


                        }
                    }
                    break;
                case DK.BT3_DISCONNECTED:
//                    Toast.makeText(MainActivity.this, "Bluetooth device has been disconnected.", Toast.LENGTH_SHORT).show();
                    break;
                case DK.BT4_ACL_DISCONNECTED:
//                    Toast.makeText(MainActivity.this, "BLE device has been disconnected.", Toast.LENGTH_SHORT).show();
                    break;
                case DK.USB_IN:
                    mHandlerMessage = "USB device has been inserted.";
//                    Toast.makeText(MainActivity.this, "USB device has been inserted.", Toast.LENGTH_SHORT).show();
                    break;
                case DK.USB_OUT:
                    mHandlerMessage = "USB device out";
//                    Toast.makeText(MainActivity.this, "USB device out", Toast.LENGTH_SHORT).show();
//                    addSpinnerPrivate(new String[]{});
                    break;
                default:
                    if ((msg.what & DK.CARD_IN_MASK) == DK.CARD_IN_MASK) {
                        //Card in.
                        mHandlerMessage = "Card slot " + (msg.what % DK.CARD_IN_MASK) + " in.";
//                        Toast.makeText(MainActivity.this, "Card slot " + (msg.what % DK.CARD_IN_MASK) + " in.", Toast.LENGTH_SHORT).show();
                        return;
                    } else if ((msg.what & DK.CARD_OUT_MASK) == DK.CARD_OUT_MASK) {
                        //Card out.
                        mHandlerMessage = "Card slot " + (msg.what % DK.CARD_IN_MASK) + " out.";
//                        Toast.makeText(MainActivity.this, "Card slot " + (msg.what % DK.CARD_IN_MASK) + " out.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;
            }
        }
    };
}
