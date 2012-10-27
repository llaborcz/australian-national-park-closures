package com.bluebottlesoftware.nationalparkclosures.data;

public class DataConsumerFactory
{
    /**
     * Returns a data consumer that corresponds to the given state
     * @param state
     * @return
     */
    public static DataConsumer createDataConsumer(int state)
    {
        DataConsumer consumer;
        switch(state)
        {
        case Region.Nsw:
        case Region.Qld:
            consumer = new RssDataFeedConsumer();
            break;
            
        default:
            throw new IllegalArgumentException("invalid state requested");
            
        }
        return consumer;
    }
}
