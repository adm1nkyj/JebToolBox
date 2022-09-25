package com.jebtoolbox;

import com.pnfsoftware.jeb.core.*;
import com.pnfsoftware.jeb.core.RuntimeProjectUtil;
import com.pnfsoftware.jeb.core.units.IUnit;
import com.pnfsoftware.jeb.rcpclient.IViewManager;
import com.pnfsoftware.jeb.rcpclient.RcpClientContext;
import com.pnfsoftware.jeb.rcpclient.dialogs.TextDialog;
import com.pnfsoftware.jeb.rcpclient.extensions.UI;
import com.pnfsoftware.jeb.rcpclient.extensions.app.model.IMPart;
import com.pnfsoftware.jeb.rcpclient.extensions.tab.TabFolderView;
import com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler;
import com.pnfsoftware.jeb.rcpclient.parts.PartManager;
import com.pnfsoftware.jeb.rcpclient.parts.UnitPartManager;
import com.pnfsoftware.jeb.rcpclient.parts.units.TextFragment;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import java.util.List;
import java.util.Map;

public class JebToolBox extends AbstractEnginesPlugin {

    public static void main(String[] args){
    }
    @Override
    public void dispose() {
    }
    @Override
    public void load(IEnginesContext context) {
        RcpClientContext rcpCLientContext = RcpClientContext.getInstance();
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                rcpCLientContext.getKeyAccelaratorManager().registerHandler(new ShortCutHandler(JebToolBox.this, "jtbkey", "noname", "notooltip", "noicon"));
            }
        });
    }

    @Override
    public void execute(IEnginesContext iEnginesContext, Map<String, String> map) {
        RcpClientContext rcpClientContext = RcpClientContext.getInstance();
        PartManager partManager = rcpClientContext.getPartManager();
        rcpClientContext.getDisplay().getDefault().asyncExec(new Runnable() {
            public void run() {
                TextDialog dlg = new TextDialog(UI.getShellTracker().get(), "JebToolBox", "", (String)null);
                dlg.setVisualBounds(0, 100, 9, 10);
                dlg.setEditable(true);
                dlg.setFont(rcpClientContext.getFontManager().getCodeFont());
                dlg.setColumnCount(50);
                dlg.setLineCount(1);
                String cmd = dlg.open();

                if(cmd != null){
                    if("wrap".equals(cmd)) {
                        try {
                            IMPart part = partManager.getActivePart();
                            if (part == null) {
                                part = partManager.getLastFocusedPart();
                            }
                            UnitPartManager view = (UnitPartManager) part.getManager();
                            TabFolderView tabForderView = view.getFolder();
                            List<TabFolderView.Entry> entries = tabForderView.getEntries();
                            Control control = null;
                            for(TabFolderView.Entry entry : entries){
                                if("Source".equals(entry.getName())){
                                    control = entry.getControl();
                                    break;
                                }
                            }
                            if(control != null) {
                                StyledText textView = ((TextFragment) control).getViewer().getTextWidget();
                                Boolean wordWrap = textView.getWordWrap();
                                if (wordWrap == true) {
                                    textView.setWordWrap(false);
                                } else {
                                    textView.setWordWrap(true);
                                }
                            }
                        }
                        catch (Exception e){
                            System.out.println(e.getMessage());
                        }
                    }
                }

            }
        });
        System.out.println("execute JebToolBox");
    }

    @Override
    public IPluginInformation getPluginInformation() {
        return new PluginInformation("JebToolBox",  "Support to your reversing", "yongjin kim",  new Version(1,0));

    }
}
class ShortCutHandler extends JebBaseHandler {
    JebToolBox context = null;
    public ShortCutHandler(JebToolBox context, String id, String name, String tooltip, String icon) {
        super(id, name, tooltip, icon);
        setAccelerator(393300); // ctrl+shift+t
        this.context = context;
    }

    @Override // com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler, com.pnfsoftware.jeb.rcpclient.extensions.binding.ActionEx
    public boolean canExecute() {
        return true;
    }

    @Override // com.pnfsoftware.jeb.rcpclient.handlers.JebBaseHandler, com.pnfsoftware.jeb.rcpclient.extensions.binding.ActionEx
    public void execute() {
        RcpClientContext context = RcpClientContext.getInstance();
        if (context == null) {
            throw new RuntimeException("The UI context cannot be retrieved");
        }
        this.context.execute(context.getEnginesContext(), null);
    }
}
