package ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resmodulator.R;

import Interface.ItemClickListener;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtOrderId,txtOrderStatus,txtOrderTableNo,txtOrderPhone,txtOrderDate;

    private ItemClickListener itemClickListener;


    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderPhone = (TextView)itemView.findViewById(R.id.order_phone_number);
        txtOrderId = (TextView)itemView.findViewById(R.id.order_id);
        txtOrderStatus = (TextView)itemView.findViewById(R.id.order_status);
        txtOrderTableNo = (TextView)itemView.findViewById(R.id.order_TableNo);
        txtOrderDate = (TextView)itemView.findViewById(R.id.order_date);

        itemView.setOnClickListener(this);



    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

      itemClickListener.onClick(v,getAdapterPosition(),false);

    }
}
