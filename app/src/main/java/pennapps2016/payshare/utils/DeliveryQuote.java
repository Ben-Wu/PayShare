package pennapps2016.payshare.utils;

public class DeliveryQuote {

    private String pickupAddress;
    private String dropoffAddress;

    public DeliveryQuote(String aPickup, String aDropoff){
        pickupAddress = aPickup;
        dropoffAddress = aDropoff;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public String getDropoffAddress() {
        return dropoffAddress;
    }
}
