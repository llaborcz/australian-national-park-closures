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
        return new RssDataFeedConsumer();
    }
}
