package project.app.pocketsocket.utils

import android.content.Context
import android.net.wifi.WifiManager
import project.app.pocketsocket.R
import java.net.InetAddress
import java.net.UnknownHostException

object Util {
    fun getIp(context: Context): String {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val dhcpInfo = wifiManager.dhcpInfo
        val ipAddress = ipAddressOrNull(dhcpInfo.ipAddress)
        return ipAddress.let {
            if(it.isNullOrEmpty()){
                context.getString(R.string.no_wifi)
            }
            else{
                it
            }
        }
    }

    private fun ipAddressOrNull(ipAddress: Int): String? {
        return when (ipAddress) {
            0 ->
                null
            else ->
                ipAddress.toInetAddress().hostAddress
        }
    }

    private fun Int.toInetAddress(): InetAddress {
        val addressBytes = byteArrayOf(
            (0xff and this).toByte(),
            (0xff and (this shr 8)).toByte(),
            (0xff and (this shr 16)).toByte(),
            (0xff and (this shr 24)).toByte()
        )
        return try {
            InetAddress.getByAddress(addressBytes)
        } catch (e: UnknownHostException) {
            throw AssertionError()
        }
    }
}