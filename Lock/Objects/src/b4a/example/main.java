package b4a.example;


import myk.b4a.B4AMenuItem;
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

public class main extends com.datasteam.b4a.system.lockscreen.MainActivity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = true;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.example", "b4a.example.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
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
		activityBA = new BA(this, layout, processBA, "b4a.example", "b4a.example.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
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
		return main.class;
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
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
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
		myk.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        myk.b4a.Msgbox.isDismissing = false;
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
        myk.b4a.Msgbox.isDismissing = false;
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
            BA.LogInfo("** Activity (main) Resume **");
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

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.Timer _timer1 = null;
public static String _x1 = "";
public static String _x2 = "";
public static String _x3 = "";
public static myk.b4a.objects.MediaPlayerWrapper _clicksound = null;
public static myk.b4a.obejcts.TTS _tts = null;
public static myk.b4a.sql.SQL _sql1 = null;
public static myk.b4a.sql.SQL.CursorWrapper _cursor1 = null;
public myk.b4a.objects.MediaPlayerWrapper _introwel = null;
public myk.b4a.objects.ProgressBarWrapper _progressbar1 = null;
public static int _num = 0;
public b4a.example.book _book = null;
public b4a.example.intro _intro = null;
public b4a.example.signaturecapture _signaturecapture = null;
public b4a.example.siglock _siglock = null;
public b4a.example.lockscreen _lockscreen = null;
public b4a.example.locksliding _locksliding = null;
public b4a.example.lockswiping _lockswiping = null;
public b4a.example.lockswipes _lockswipes = null;
public b4a.example.viewmap _viewmap = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (book.mostCurrent != null);
vis = vis | (intro.mostCurrent != null);
vis = vis | (siglock.mostCurrent != null);
vis = vis | (lockscreen.mostCurrent != null);
vis = vis | (locksliding.mostCurrent != null);
vis = vis | (lockswiping.mostCurrent != null);
vis = vis | (lockswipes.mostCurrent != null);
vis = vis | (viewmap.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 45;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 47;BA.debugLine="Activity.LoadLayout(\"intro\")";
mostCurrent._activity.LoadLayout("intro",mostCurrent.activityBA);
 //BA.debugLineNum = 48;BA.debugLine="introwel.Initialize2(\"introwel\")";
mostCurrent._introwel.Initialize2(processBA,"introwel");
 //BA.debugLineNum = 49;BA.debugLine="introwel.Load(File.DirAssets, \"intro.mp3\")";
mostCurrent._introwel.Load(myk.b4a.keywords.Common.File.getDirAssets(),"intro.mp3");
 //BA.debugLineNum = 50;BA.debugLine="timer1.Initialize(\"timer1\",50)";
_timer1.Initialize(processBA,"timer1",(long) (50));
 //BA.debugLineNum = 51;BA.debugLine="timer1.Enabled = True";
_timer1.setEnabled(myk.b4a.keywords.Common.True);
 //BA.debugLineNum = 53;BA.debugLine="ProgressBar1.Top = 94%y";
mostCurrent._progressbar1.setTop(myk.b4a.keywords.Common.PerYToCurrent((float) (94),mostCurrent.activityBA));
 //BA.debugLineNum = 54;BA.debugLine="ProgressBar1.Left  = 0%x";
mostCurrent._progressbar1.setLeft(myk.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA));
 //BA.debugLineNum = 55;BA.debugLine="ProgressBar1.Width = 100%x";
mostCurrent._progressbar1.setWidth(myk.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 56;BA.debugLine="TTS.Initialize(\"tts\")";
_tts.Initialize(processBA,"tts");
 //BA.debugLineNum = 60;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 66;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 68;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 62;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 64;BA.debugLine="End Sub";
return "";
}
public static String  _btnsearch_click() throws Exception{
 //BA.debugLineNum = 99;BA.debugLine="Sub btnSearch_Click";
 //BA.debugLineNum = 101;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
book._process_globals();
intro._process_globals();
signaturecapture._process_globals();
siglock._process_globals();
lockscreen._process_globals();
locksliding._process_globals();
lockswiping._process_globals();
lockswipes._process_globals();
viewmap._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _globals() throws Exception{
 //BA.debugLineNum = 31;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 34;BA.debugLine="Dim  introwel As MediaPlayer";
mostCurrent._introwel = new myk.b4a.objects.MediaPlayerWrapper();
 //BA.debugLineNum = 37;BA.debugLine="Dim ProgressBar1 As ProgressBar";
mostCurrent._progressbar1 = new anywheresoftware.b4a.objects.ProgressBarWrapper();
 //BA.debugLineNum = 38;BA.debugLine="Dim num As Int";
_num = 0;
 //BA.debugLineNum = 39;BA.debugLine="clicksound.Initialize2(\"clicksound\")";
_clicksound.Initialize2(processBA,"clicksound");
 //BA.debugLineNum = 40;BA.debugLine="clicksound.Load(File.DirAssets, \"click.mp3\")";
_clicksound.Load(myk.b4a.keywords.Common.File.getDirAssets(),"click.mp3");
 //BA.debugLineNum = 43;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 18;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 21;BA.debugLine="Dim timer1 As Timer";
_timer1 = new myk.b4a.objects.Timer();
 //BA.debugLineNum = 22;BA.debugLine="Public x1, x2, x3 As String";
_x1 = "";
_x2 = "";
_x3 = "";
 //BA.debugLineNum = 24;BA.debugLine="Public clicksound As MediaPlayer";
_clicksound = new myk.b4a.objects.MediaPlayerWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Public TTS As TTS";
_tts = new myk.b4a.obejcts.TTS();
 //BA.debugLineNum = 27;BA.debugLine="Public sql1 As SQL";
_sql1 = new myk.b4a.sql.SQL();
 //BA.debugLineNum = 28;BA.debugLine="Public cursor1 As Cursor";
_cursor1 = new myk.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 29;BA.debugLine="End Sub";
return "";
}
public static String  _timer1_tick() throws Exception{
 //BA.debugLineNum = 70;BA.debugLine="Sub timer1_tick";
 //BA.debugLineNum = 71;BA.debugLine="num = num +1";
_num = (int) (_num+1);
 //BA.debugLineNum = 72;BA.debugLine="ProgressBar1.Progress = num";
mostCurrent._progressbar1.setProgress(_num);
 //BA.debugLineNum = 74;BA.debugLine="If ProgressBar1.Progress == 2 Then";
if (mostCurrent._progressbar1.getProgress()==2) { 
 //BA.debugLineNum = 76;BA.debugLine="introwel.Play";
mostCurrent._introwel.Play();
 };
 //BA.debugLineNum = 80;BA.debugLine="If ProgressBar1.Progress == 70 Then";
if (mostCurrent._progressbar1.getProgress()==70) { 
 //BA.debugLineNum = 82;BA.debugLine="TTS.Speak(\"This is an electronic signature lock\",";
_tts.Speak("This is an electronic signature lock",myk.b4a.keywords.Common.True);
 //BA.debugLineNum = 83;BA.debugLine="ToastMessageShow(\"This is an electronic signature";
myk.b4a.keywords.Common.ToastMessageShow("This is an electronic signature lock",myk.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 88;BA.debugLine="If ProgressBar1.Progress == 100 Then";
if (mostCurrent._progressbar1.getProgress()==100) { 
 //BA.debugLineNum = 89;BA.debugLine="timer1.Enabled = False";
_timer1.setEnabled(myk.b4a.keywords.Common.False);
 //BA.debugLineNum = 91;BA.debugLine="introwel.Stop";
mostCurrent._introwel.Stop();
 //BA.debugLineNum = 92;BA.debugLine="StartActivity(\"Book\")";
myk.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)("Book"));
 //BA.debugLineNum = 93;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 };
 //BA.debugLineNum = 95;BA.debugLine="End Sub";
return "";
}
}
