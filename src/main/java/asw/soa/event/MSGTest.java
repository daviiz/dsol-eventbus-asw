package asw.soa.event;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MSGTest {
	@Subscribe(threadMode = ThreadMode.ASYNC)
	public void onMessageEvent(MessageEvent event) {
	    System.out.println(event.message);
	}
	
	public static void main(String[] args) {
		EventBus.getDefault().register(new MSGTest());
		
		EventBus.getDefault().post(new MessageEvent("Hello everyone!"));
	}
}
/**
*
*			this.devsSimulator.scheduleEventAbs(1.0d * i, new Executable()
            {
                @Override
                public void execute()
                {
                    print();
                }
            });
*
*/