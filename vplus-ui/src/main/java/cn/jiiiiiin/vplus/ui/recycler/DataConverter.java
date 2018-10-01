package cn.jiiiiiin.vplus.ui.recycler;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;

import cn.jiiiiiin.vplus.core.dict.Err;

/**
 * 数据转换器基类,在具体的Delegate视图中完成数据转换
 * 转换Adapter真实需要的数据
 *
 * @author jiiiiiin
 */

public abstract class DataConverter {

    protected final ArrayList<MultipleItemEntity> ENTITIES = new ArrayList<>();
    private JSONObject mJsonData = null;

    /**
     * 根据json数据转换成MultipleItemEntities
     * @return
     */
    public abstract ArrayList<MultipleItemEntity> convert();

    public DataConverter setJsonData(JSONObject json) {
        this.mJsonData = json;
        return this;
    }

    protected JSONObject getJsonData() {
        if (mJsonData == null || mJsonData.isEmpty()) {
            throw new NullPointerException("DATA IS NULL");
        }
        return mJsonData;
    }

    public void clearData(){
        ENTITIES.clear();
    }
}
