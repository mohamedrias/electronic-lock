package b4a.example;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import myk.b4a.BA;
import myk.b4a.BALayout;
import myk.b4a.B4AActivity;
import myk.b4a.ObjectWrapper;
import myk.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import myk.b4a.B4AUncaughtException;
import myk.b4a.debug.*;
import java.lang.ref.WeakReference;

public class lockscreen extends com.datasteam.b4a.system.lockscreen.LockScreenActivity implements B4AActivity{
	public static lockscreen mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.example", "b4a.example.lockscreen");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (lockscreen).");
				p.finish();
			}
		}
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
		BA.handler.postDelayed(new WaitForLayout(), 5);

	}
	private static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "b4a.example", "b4a.example.lockscreen");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.lockscreen", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (lockscreen) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (lockscreen) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return lockscreen.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == myk.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (lockscreen) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (lockscreen) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}

public myk.b4a.keywords.Common __c = null;
public static myk.b4a.objects.Timer _tmclock = null;
public myk.b4a.objects.LabelWrapper _lbclock = null;
public b4a.example.slidebutton _btnslide = null;
public b4a.example.main _main = null;
public b4a.example.book _book = null;
public b4a.example.intro _intro = null;
public b4a.example.signaturecapture _signaturecapture = null;
public b4a.example.siglock _siglock = null;
public b4a.example.locksliding _locksliding = null;
public b4a.example.lockswiping _lockswiping = null;
public b4a.example.lockswipes _lockswipes = null;
public b4a.example.viewmap _viewmap = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 14;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 15;BA.debugLine="Activity.LoadLayout(\"LockScreen1\")";
mostCurrent._activity.LoadLayout("LockScreen1",mostCurrent.activityBA);
 //BA.debugLineNum = 18;BA.debugLine="LbClock.Typeface = Typeface.LoadFromAssets(\"digi.";
mostCurrent._lbclock.setTypeface(anywheresoftware.b4a.keywords.Common.Typeface.LoadFromAssets("digi.ttf"));
 //BA.debugLineNum = 18;BA.debugLine="LbClock.Typeface = Typeface.LoadFromAssets(\"digi.";
mostCurrent._lbclock.setTextSize((float) (120));
 //BA.debugLineNum = 19;BA.debugLine="TmClock.Initialize(\"Clock\", 1000) : TmClock.Enabl";
_tmclock.Initialize(processBA,"Clock",(long) (1000));
 //BA.debugLineNum = 19;BA.debugLine="TmClock.Initialize(\"Clock\", 1000) : TmClock.Enabl";
_tmclock.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 19;BA.debugLine="TmClock.Initialize(\"Clock\", 1000) : TmClock.Enabl";
_clock_tick();
 //BA.debugLineNum = 21;BA.debugLine="BtnSlide.SetThreshold(80)";
mostCurrent._btnslide._setthreshold((int) (80));
 //BA.debugLineNum = 22;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 27;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 28;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 24;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 25;BA.debugLine="End Sub";
return "";
}
public static String  _btnlock_click() throws Exception{
 //BA.debugLineNum = 43;BA.debugLine="Sub BtnLock_Click";
 //BA.debugLineNum = 44;BA.debugLine="lockSliding.Controller.Lock";
mostCurrent._locksliding._controller.Lock();
 //BA.debugLineNum = 45;BA.debugLine="End Sub";
return "";
}
public static String  _btnslide_checkpoint() throws Exception{
 //BA.debugLineNum = 34;BA.debugLine="Sub BtnSlide_CheckPoint";
 //BA.debugLineNum = 35;BA.debugLine="Vibrate";
_vibrate();
 //BA.debugLineNum = 36;BA.debugLine="End Sub";
return "";
}
public static String  _btnslide_unlock() throws Exception{
 //BA.debugLineNum = 38;BA.debugLine="Sub BtnSlide_Unlock";
 //BA.debugLineNum = 39;BA.debugLine="Vibrate";
_vibrate();
 //BA.debugLineNum = 40;BA.debugLine="lockSliding.Controller.Unlock";
mostCurrent._locksliding._controller.Unlock();
 //BA.debugLineNum = 41;BA.debugLine="End Sub";
return "";
}
public static String  _clock_tick() throws Exception{
 //BA.debugLineNum = 47;BA.debugLine="Sub Clock_Tick";
 //BA.debugLineNum = 48;BA.debugLine="Try";
try { //BA.debugLineNum = 49;BA.debugLine="DateTime.TimeFormat = \"hh:mm\"";
anywheresoftware.b4a.keywords.Common.DateTime.setTimeFormat("hh:mm");
 //BA.debugLineNum = 50;BA.debugLine="LbClock.Text = DateTime.Time(DateTime.Now)";
mostCurrent._lbclock.setText((Object)(myk.b4a.keywords.Common.DateTime.Time(myk.b4a.keywords.Common.DateTime.getNow())));
 } 
       catch (Exception e39) {
			processBA.setLastException(e39); //BA.debugLineNum = 52;BA.debugLine="Log(LastException.Message)";
myk.b4a.keywords.Common.Log(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage());
 };
 //BA.debugLineNum = 54;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 9;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 10;BA.debugLine="Dim LbClock As Label";
mostCurrent._lbclock = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 11;BA.debugLine="Private BtnSlide As SlideButton";
mostCurrent._btnslide = new b4a.example.slidebutton();
 //BA.debugLineNum = 12;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 5;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 6;BA.debugLine="Dim TmClock As Timer";
_tmclock = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 7;BA.debugLine="End Sub";
return "";
}
public static String  _vibrate() throws Exception{
myk.b4a.phone.Phone.PhoneVibrate _vibrator = null;
 //BA.debugLineNum = 30;BA.debugLine="Sub Vibrate";
 //BA.debugLineNum = 31;BA.debugLine="Dim Vibrator As PhoneVibrate : Vibrator.Vibrate(1";
_vibrator = new anywheresoftware.b4a.phone.Phone.PhoneVibrate();
 //BA.debugLineNum = 31;BA.debugLine="Dim Vibrator As PhoneVibrate : Vibrator.Vibrate(1";
_vibrator.Vibrate(processBA,(long) (100));
 //BA.debugLineNum = 32;BA.debugLine="End Sub";
return "";
}
}
