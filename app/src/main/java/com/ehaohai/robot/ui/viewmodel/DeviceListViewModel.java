package com.ehaohai.robot.ui.viewmodel;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.ehaohai.robot.base.BaseViewModel;
import com.ehaohai.robot.model.Device;

import java.util.ArrayList;
import java.util.List;

public class DeviceListViewModel extends BaseViewModel {
    public Context context;
    public final MutableLiveData<String> device = new MutableLiveData<>();

    public List<Device> deviceList = new ArrayList<>();

    public void start(Context context) {
        this.context = context;

        postDeviceList();
    }

    public void postDeviceList(){
        deviceList.add(new Device("1","浩海机器狗","来自共享","https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=%E6%9C%BA%E5%99%A8%E7%8B%97&step_word=&hs=0&pn=2&spn=0&di=7466852183703552001&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=2801055612%2C2123440719&os=3389777087%2C1883788223&simid=4119708698%2C482448627&adpicid=0&lpn=0&ln=1114&fr=&fmq=1742524204442_R&fm=&ic=undefined&s=undefined&hd=undefined&latest=undefined&copyright=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=https%3A%2F%2Fbkimg.cdn.bcebos.com%2Fpic%2F48540923dd54564e925839c611868b82d158cdbfe181&fromurl=ippr_z2C%24qAzdH3FAzdH3Fkwthj_z%26e3Bkwt17_z%26e3Bv54AzdH3Ftpj4AzdH3F%25Ec%25AE%25b0%25Em%25Aa%25l8%25Em%25lC%25BA%25Ec%25ll%25Ab%25E0%25bB%25l0AzdH3Fmnl80l8m&gsm=1e&rpstart=0&rpnum=0&islist=&querylist=&nojc=undefined&dyTabStr=MCwxMiwzLDEsMiwxMyw3LDYsNSw5&lid=8646775551419468731","1","Go2 EDU"));
        deviceList.add(new Device("2","轮式机器人","来自共享","https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=%E6%9C%BA%E5%99%A8%E7%8B%97&step_word=&hs=0&pn=10&spn=0&di=7466852183703552001&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=3520656532%2C2052925491&os=3862625615%2C2989820593&simid=4136018453%2C478301500&adpicid=0&lpn=0&ln=1114&fr=&fmq=1742524279198_R&fm=&ic=undefined&s=undefined&hd=undefined&latest=undefined&copyright=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=https%3A%2F%2Fimg.ithome.com%2Fnewsuploadfiles%2F2024%2F4%2F13170e00-7feb-4609-be2f-65feeb2caf58.png%3Fx-bce-process%3Dimage%2Fformat%2Cf_auto&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3Btpi54j_z%26e3Bv54AzdH3FaAzdH3F0m9AzdH3Fca8_z%26e3Bip4&gsm=1e&rpstart=0&rpnum=0&islist=&querylist=&nojc=undefined&dyTabStr=MCwxMiwzLDEsMiwxMyw3LDYsNSw5&lid=11377975415173243272","1","1888"));
        deviceList.add(new Device("3","浩海机器狗","来自共享","https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=%E6%9C%BA%E5%99%A8%E7%8B%97&step_word=&hs=0&pn=2&spn=0&di=7466852183703552001&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=2801055612%2C2123440719&os=3389777087%2C1883788223&simid=4119708698%2C482448627&adpicid=0&lpn=0&ln=1114&fr=&fmq=1742524204442_R&fm=&ic=undefined&s=undefined&hd=undefined&latest=undefined&copyright=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=https%3A%2F%2Fbkimg.cdn.bcebos.com%2Fpic%2F48540923dd54564e925839c611868b82d158cdbfe181&fromurl=ippr_z2C%24qAzdH3FAzdH3Fkwthj_z%26e3Bkwt17_z%26e3Bv54AzdH3Ftpj4AzdH3F%25Ec%25AE%25b0%25Em%25Aa%25l8%25Em%25lC%25BA%25Ec%25ll%25Ab%25E0%25bB%25l0AzdH3Fmnl80l8m&gsm=1e&rpstart=0&rpnum=0&islist=&querylist=&nojc=undefined&dyTabStr=MCwxMiwzLDEsMiwxMyw3LDYsNSw5&lid=8646775551419468731","1","Go2 EDU"));
        deviceList.add(new Device("4","浩海机器狗","来自共享","https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=%E6%9C%BA%E5%99%A8%E7%8B%97&step_word=&hs=0&pn=2&spn=0&di=7466852183703552001&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=2801055612%2C2123440719&os=3389777087%2C1883788223&simid=4119708698%2C482448627&adpicid=0&lpn=0&ln=1114&fr=&fmq=1742524204442_R&fm=&ic=undefined&s=undefined&hd=undefined&latest=undefined&copyright=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=https%3A%2F%2Fbkimg.cdn.bcebos.com%2Fpic%2F48540923dd54564e925839c611868b82d158cdbfe181&fromurl=ippr_z2C%24qAzdH3FAzdH3Fkwthj_z%26e3Bkwt17_z%26e3Bv54AzdH3Ftpj4AzdH3F%25Ec%25AE%25b0%25Em%25Aa%25l8%25Em%25lC%25BA%25Ec%25ll%25Ab%25E0%25bB%25l0AzdH3Fmnl80l8m&gsm=1e&rpstart=0&rpnum=0&islist=&querylist=&nojc=undefined&dyTabStr=MCwxMiwzLDEsMiwxMyw3LDYsNSw5&lid=8646775551419468731","1","Go2 EDU"));
        deviceList.add(new Device("5","浩海机器狗","来自共享","https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=%E6%9C%BA%E5%99%A8%E7%8B%97&step_word=&hs=0&pn=2&spn=0&di=7466852183703552001&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=2801055612%2C2123440719&os=3389777087%2C1883788223&simid=4119708698%2C482448627&adpicid=0&lpn=0&ln=1114&fr=&fmq=1742524204442_R&fm=&ic=undefined&s=undefined&hd=undefined&latest=undefined&copyright=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=https%3A%2F%2Fbkimg.cdn.bcebos.com%2Fpic%2F48540923dd54564e925839c611868b82d158cdbfe181&fromurl=ippr_z2C%24qAzdH3FAzdH3Fkwthj_z%26e3Bkwt17_z%26e3Bv54AzdH3Ftpj4AzdH3F%25Ec%25AE%25b0%25Em%25Aa%25l8%25Em%25lC%25BA%25Ec%25ll%25Ab%25E0%25bB%25l0AzdH3Fmnl80l8m&gsm=1e&rpstart=0&rpnum=0&islist=&querylist=&nojc=undefined&dyTabStr=MCwxMiwzLDEsMiwxMyw3LDYsNSw5&lid=8646775551419468731","1","Go2 EDU"));
        deviceList.add(new Device("6","浩海机器狗","来自共享","https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=%E6%9C%BA%E5%99%A8%E7%8B%97&step_word=&hs=0&pn=2&spn=0&di=7466852183703552001&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=0&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=undefined&cs=2801055612%2C2123440719&os=3389777087%2C1883788223&simid=4119708698%2C482448627&adpicid=0&lpn=0&ln=1114&fr=&fmq=1742524204442_R&fm=&ic=undefined&s=undefined&hd=undefined&latest=undefined&copyright=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=https%3A%2F%2Fbkimg.cdn.bcebos.com%2Fpic%2F48540923dd54564e925839c611868b82d158cdbfe181&fromurl=ippr_z2C%24qAzdH3FAzdH3Fkwthj_z%26e3Bkwt17_z%26e3Bv54AzdH3Ftpj4AzdH3F%25Ec%25AE%25b0%25Em%25Aa%25l8%25Em%25lC%25BA%25Ec%25ll%25Ab%25E0%25bB%25l0AzdH3Fmnl80l8m&gsm=1e&rpstart=0&rpnum=0&islist=&querylist=&nojc=undefined&dyTabStr=MCwxMiwzLDEsMiwxMyw3LDYsNSw5&lid=8646775551419468731","1","Go2 EDU"));
        device.postValue(device.getValue()+"-");
    }

}
