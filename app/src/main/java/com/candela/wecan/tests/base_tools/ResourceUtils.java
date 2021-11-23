package com.candela.wecan.tests.base_tools;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.HardwarePropertiesManager;
import android.os.Parcel;
import android.telephony.CellIdentity;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import candela.lfresource.AndroidUI;
import candela.lfresource.PlatformInfo;
import candela.lfresource.StringKeyVal;
import candela.lfresource.PlatformInfo;

public class ResourceUtils extends AppCompatActivity implements AndroidUI{
    public static Context context;

    public ResourceUtils(Context context){
        this.context = context;
    }

    @Override
    public void setResourceInfo(int i, int i1) {
        // TODO:  Store this info for next time.
    }

    @SuppressLint({"HardwareIds", "MissingPermission", "NewApi"})
    @Override
    public Vector<StringKeyVal> requestPortUpdate(String s) {
        Vector<StringKeyVal> data_structure = new Vector<StringKeyVal>();
        if (s.equals("wlan0")){
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            data_structure.add(new StringKeyVal("SSID",wifiManager.getConnectionInfo().getSSID().replaceAll("\"","")));
            data_structure.add(new StringKeyVal("BSSID",wifiManager.getConnectionInfo().getBSSID()));
            data_structure.add(new StringKeyVal("RSSI",String.valueOf(wifiManager.getConnectionInfo().getRssi())));
            data_structure.add(new StringKeyVal("Frequency",String.valueOf(wifiManager.getConnectionInfo().getFrequency())));
            data_structure.add(new StringKeyVal("Link speed",String.valueOf(wifiManager.getConnectionInfo().getLinkSpeed())));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                data_structure.add(new StringKeyVal("Tx Link speed",String.valueOf(wifiManager.getConnectionInfo().getTxLinkSpeedMbps())));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                data_structure.add(new StringKeyVal("Wi-Fi standard",String.valueOf(wifiManager.getConnectionInfo().getWifiStandard())));
                data_structure.add(new StringKeyVal("Max Supported Rx Link speed",String.valueOf(wifiManager.getConnectionInfo().getMaxSupportedRxLinkSpeedMbps())));
            }
            data_structure.add(new StringKeyVal("DHCP-IPv4",String.valueOf(wifiManager.getDhcpInfo().ipAddress)));
            data_structure.add(new StringKeyVal("DHCP-Gateway",String.valueOf(wifiManager.getDhcpInfo().gateway)));
            data_structure.add(new StringKeyVal("DHCP-DNS1",String.valueOf(wifiManager.getDhcpInfo().dns1)));
            data_structure.add(new StringKeyVal("DHCP-DNS2",String.valueOf(wifiManager.getDhcpInfo().dns2)));
            data_structure.add(new StringKeyVal("DHCP-Lease-Duration",String.valueOf(wifiManager.getDhcpInfo().leaseDuration)));
            data_structure.add(new StringKeyVal("DHCP-Server",String.valueOf(wifiManager.getDhcpInfo().serverAddress)));

            System.out.println(data_structure);
            return data_structure;
        }
        if (s.equals("wiphy0")){
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            List<NetworkInterface> all = null;
            try {
                all = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface nif : all) {
                    if (nif.getName().equals("wlan0")){
                        System.out.println(nif.getName());
                        System.out.println(nif.getInetAddresses().toString());
                        data_structure.add(new StringKeyVal("Hardware-Address",Base64.getEncoder().encodeToString(nif.getHardwareAddress())));
                        data_structure.add(new StringKeyVal("MTU", String.valueOf(nif.getMTU())));
                        data_structure.add(new StringKeyVal("is-P2P", String.valueOf(nif.isPointToPoint())));
                        data_structure.add(new StringKeyVal("Supports-Multicast", String.valueOf(nif.supportsMulticast())));
                        data_structure.add(new StringKeyVal("Up", String.valueOf(nif.isUp())));
                        data_structure.add(new StringKeyVal("Hardware", String.valueOf(Build.HARDWARE)));

                    }

                }
            } catch (SocketException e) {
                e.printStackTrace();
            }

            return data_structure;
        }
        else if (s.startsWith("epdg") || (s.startsWith("rmnet"))){
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            data_structure.add(new StringKeyVal("Network-Operator",String.valueOf(telephonyManager.getNetworkOperatorName())));
            List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();
            String strength = "";
            if(cellInfos!=null){
                for (int i = 0 ; i<cellInfos.size(); i++){
                    if (cellInfos.get(i).isRegistered()){
                        if(cellInfos.get(i) instanceof CellInfoWcdma){
                            CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) telephonyManager.getAllCellInfo().get(0);
                            CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
                            strength = String.valueOf(cellSignalStrengthWcdma.getDbm());
                        }else if(cellInfos.get(i) instanceof CellInfoGsm){
                            CellInfoGsm cellInfogsm = (CellInfoGsm) telephonyManager.getAllCellInfo().get(0);
                            CellSignalStrengthGsm cellSignalStrengthGsm = cellInfogsm.getCellSignalStrength();
                            strength = String.valueOf(cellSignalStrengthGsm.getDbm());
                        }else if(cellInfos.get(i) instanceof CellInfoLte){
                            CellInfoLte cellInfoLte = (CellInfoLte) telephonyManager.getAllCellInfo().get(0);
                            CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                            strength = String.valueOf(cellSignalStrengthLte.getDbm());
                        }
                    }
                }
                data_structure.add(new StringKeyVal("Signal-Strength",String.valueOf(strength)));
            }
            return data_structure;
        }
        else{
            return data_structure;
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint({"WifiManagerLeak", "NewApi", "HardwareIds", "MissingPermission"})
    public PlatformInfo requestPlatformUpdate() {
        PlatformInfo pi = new PlatformInfo();

        // TODO:  Fix me, PlatformInfo, Build and WifiManager objects provide useful info.

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String product = Build.PRODUCT;
        String username = Build.USER;
        String release = Build.VERSION.RELEASE;
        String version_incremental = Build.VERSION.INCREMENTAL;
        int version_sdk_number = Build.VERSION.SDK_INT;
        String board = Build.BOARD;
        String brand = Build.BRAND;
        String cpu_abi = Build.CPU_ABI;
        String[] cpu_abi2 = (Build.SUPPORTED_ABIS);
        String hardware = Build.HARDWARE;
        String host = Build.HOST;
        String id = Build.ID;
        long availMem  = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long totalMem  = Runtime.getRuntime().totalMemory();
        Vector<StringKeyVal> wifi_capabilities = new Vector<StringKeyVal>();
        Vector<StringKeyVal> wifi_encryption = new Vector<StringKeyVal>();

        pi.wifi_capabilities = wifi_capabilities;

        pi.manufacturer = Build.MANUFACTURER;
        pi.model = Build.MODEL;
        pi.product = Build.PRODUCT;
        pi.username = Build.USER;
        pi.release = Build.VERSION.RELEASE;
        pi.version_incremental = Build.VERSION.INCREMENTAL;
        pi.version_sdk_number = String.valueOf(Build.VERSION.SDK_INT);
        pi.board = Build.BOARD;
        pi.brand = Build.BRAND;
        pi.cpu_abi = Build.CPU_ABI;
        pi.cpu_abi2 = Build.CPU_ABI2;
        pi.hardware = Build.HARDWARE;
        pi.host = Build.HOST;
        pi.id = Build.ID;
        pi.availMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        pi.totalMem = Runtime.getRuntime().totalMemory();
//        WIFI-CAPABILITIES

//        List<ScanResult> scanResults = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).getScanResults();
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        Boolean AC_11 = wifiManager.isWifiStandardSupported(ScanResult.WIFI_STANDARD_11AC);
        Boolean AX_11 = wifiManager.isWifiStandardSupported(ScanResult.WIFI_STANDARD_11AX);
        Boolean N_11 = wifiManager.isWifiStandardSupported(ScanResult.WIFI_STANDARD_11N);
        Boolean legacy = wifiManager.isWifiStandardSupported(ScanResult.WIFI_STANDARD_LEGACY);

        wifi_capabilities.add(new StringKeyVal("supports_5G", String.valueOf((wifiManager.is5GHzBandSupported()))));
        wifi_capabilities.add(new StringKeyVal("supports_6G", String.valueOf((wifiManager.is6GHzBandSupported()))));

        if (Build.VERSION.SDK_INT >= 31) {
           // This was added in API 31, I guess before then 2.4 was always supported.
           wifi_capabilities.add(new StringKeyVal("supports_2G", String.valueOf((wifiManager.is24GHzBandSupported())))); // This line gives an error
        }
        else {
           wifi_capabilities.add(new StringKeyVal("supports_2G", String.valueOf(true)));
        }

        wifi_capabilities.add(new StringKeyVal("11-AC", String.valueOf(AC_11)));
        wifi_capabilities.add(new StringKeyVal("11-AX", String.valueOf(AX_11)));
        wifi_capabilities.add(new StringKeyVal("11-N", String.valueOf(N_11)));
        wifi_capabilities.add(new StringKeyVal("LEGACY", String.valueOf(legacy)));

//        WIFI-ENCRYPTION
        Boolean wpa3sea = wifiManager.isWpa3SaeSupported();
        Boolean Wpa3SuiteB = wifiManager.isWpa3SuiteBSupported();
        Boolean passpoint = wifiManager.isP2pSupported();
        wifi_encryption.add(new StringKeyVal("wpa3sea", String.valueOf(wpa3sea)));
        wifi_encryption.add(new StringKeyVal("Wpa3SuiteB", String.valueOf(Wpa3SuiteB)));
        wifi_encryption.add(new StringKeyVal("passpoint", String.valueOf(passpoint)));

        if (Build.VERSION.SDK_INT >= 31){
            wifi_encryption.add(new StringKeyVal("Wpa3SaeH2e", String.valueOf(true)));
        }
        else {
            wifi_encryption.add(new StringKeyVal("Wpa3SaeH2e", String.valueOf(true)));
        }

// for debugging printing in logcat

        System.out.println("manufacturer:" + manufacturer + "\n" + "model: " + model
                + "\n" + "product: " + product + "\n" + "username: " + username + "\n" + "release: "
                + release +  "\n" + "version_incremental: " + version_incremental + "\n" +
                "version_sdk_number: " + version_sdk_number + "\n" + "board: " + board + "\n" +
                "brand: " + brand + "\n" + "cpu_abi: " + cpu_abi + "\n" + "cpu_abi2: " + cpu_abi2 + "\n" +
               "hardware: " + hardware + "\n" + "host: " + host + "\n" + "id: " + id + "\n" +
                "availMem: " + availMem + "\n" + "totalMem: " + totalMem + "\n" + "wifi_capabilities: "
                + wifi_capabilities + "\n" + "wifi_encryption: " + wifi_encryption);

        return pi;
    }
}

