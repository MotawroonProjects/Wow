package com.creative_share_apps.wow.singletone;

import com.creative_share_apps.wow.models.ItemModel;

import java.util.ArrayList;
import java.util.List;

public class OrderModelSingleTone {

    private static OrderModelSingleTone instance = null;
    private List<ItemModel> itemList = new ArrayList<>();

    private OrderModelSingleTone() {
    }

    public static synchronized OrderModelSingleTone newInstance()
    {
        if (instance == null)
        {
            instance = new OrderModelSingleTone();
        }
        return instance;

    }

    public void Add_Item(ItemModel itemModel)
    {
        if (this.itemList.size()==0)
        {
            this.itemList.add(itemModel);
        }else
            {
                int pos = getItemPos(itemModel);

                if (pos == -1)
                {
                    this.itemList.add(itemModel);

                }else
                    {
                        ItemModel old_item = this.itemList.get(pos);
                        int old_quantity = old_item.getQuantity();
                        int new_quantity = itemModel.getQuantity();

                        int total_quantity = old_quantity+new_quantity;
                        double new_total_cost = total_quantity*itemModel.getProduct_price();


                        itemModel.setQuantity(total_quantity);
                        itemModel.setTotal_cost(new_total_cost);

                        this.itemList.set(pos,itemModel);

                    }

            }
    }

    public void Update_Item(ItemModel itemModel,int pos)
    {
        if (this.itemList.size()>0)
        {
            this.itemList.set(pos,itemModel);

        }
    }
    public void removeItem(int pos)
    {
        this.itemList.remove(pos);
    }
    private int getItemPos(ItemModel itemModel)
    {
        int pos = -1;

        for (int index = 0 ;index < this.itemList.size();index++)
        {
            ItemModel item = this.itemList.get(index);

            if (item.getProduct_id().equals(itemModel.getProduct_id()))
            {
                pos = index;
                break;
            }
        }

        return pos;
    }

    public List<ItemModel> getItemsList()
    {
        return this.itemList;
    }
    public int getItemCount()
    {
        return itemList.size();
    }
    public void clear()
    {
        itemList.clear();
    }

}
