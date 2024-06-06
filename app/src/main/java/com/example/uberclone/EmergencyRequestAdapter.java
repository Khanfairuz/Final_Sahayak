package com.example.uberclone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.example.uberclone.EmergencyRequest;
import java.util.List;

public class EmergencyRequestAdapter extends BaseAdapter {
    private List<EmergencyRequest> emergencyRequests;
    private Context context;
    private int selectedPosition = -1;

    public EmergencyRequestAdapter(Context context, List<EmergencyRequest> emergencyRequests) {
        this.context = context;
        this.emergencyRequests = emergencyRequests;
    }

    @Override
    public int getCount() {
        return emergencyRequests.size();
    }

    @Override
    public Object getItem(int position) {
        return emergencyRequests.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_emergency_request, parent, false);
        }

        EmergencyRequest request = emergencyRequests.get(position);

        TextView textTimeWhen = convertView.findViewById(R.id.text_time_when);
        TextView textHoursNeeded = convertView.findViewById(R.id.text_hours_needed);
        TextView textTotalPayment = convertView.findViewById(R.id.text_total_payment);
        Button buttonDelete = convertView.findViewById(R.id.button_delete);
        Button buttonShowCustomerDetails = convertView.findViewById(R.id.button_show_customer_details);

        textTimeWhen.setText("Time When: " + request.getTimeWhen());
        textHoursNeeded.setText("Hours Needed: " + request.getHoursNeeded());
        textTotalPayment.setText("Total Payment: " + request.getTotalPayment());

        buttonDelete.setOnClickListener(v -> {
            selectedPosition = position;
            // Implement delete functionality here
            if (context instanceof CartEmergencyService) {
                ((CartEmergencyService) context).removeRequest(request);
            }
        });

        buttonShowCustomerDetails.setOnClickListener(v -> {
            selectedPosition = position;
            // Implement show customer details functionality here
            if (context instanceof CartEmergencyService) {
                ((CartEmergencyService) context).showCustomerDetails(request.getUserId());
            }
        });

        return convertView;
    }

    public EmergencyRequest getSelectedRequest() {
        if (selectedPosition >= 0 && selectedPosition < emergencyRequests.size()) {
            return emergencyRequests.get(selectedPosition);
        }
        return null;
    }

    public void removeRequest(EmergencyRequest request) {
        emergencyRequests.remove(request);
        notifyDataSetChanged();
    }
}
