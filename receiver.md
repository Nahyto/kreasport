
## In main ##

    receiver = new ItemClickReceiver();
    
    IntentFilter filter = new IntentFilter(KEY_ON_ITEM_CLICK);
    
    getActivity().registerReceiver(receiver, filter);


## In listener ##

    Intent intent = new Intent(KEY_ON_ITEM_CLICK);
    intent.putExtra(KEY_ON_ITEM_CLICK_INDEX, index);
    getActivity().sendBroadcast(intent);

## receiver  ##
 private class ItemClickReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            int index = intent.getIntExtra(KEY_ON_ITEM_CLICK_INDEX, -1);
            if (index != -1) {
                OverlayItem item = mParcoursOverlay.getItem(index);
                Toast.makeText(getContext(), "Item '" + item.getTitle() + "' (index=" + index + ") got single tapped up", Toast.LENGTH_LONG).show();
            }

        }
    }