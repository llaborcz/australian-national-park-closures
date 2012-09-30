package com.bluebottlesoftware.nationalparkclosures.data;

public class DataConsumerFactory
{
    /**
     * Returns a data consumer that corresponds to the given state
     * @param state
     * @return
     */
    public static DataConsumer createDataConsumer(State state)
    {
        DataConsumer consumer;
        switch(state)
        {
        case Nsw:
            consumer = new NswDataConsumer();
            break;
            
        default:
            throw new IllegalArgumentException("invalid state requested");
            
        }
        return consumer;
    }
}
