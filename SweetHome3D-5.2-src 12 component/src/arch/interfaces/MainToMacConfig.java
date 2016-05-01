package arch.interfaces;

import javax.swing.JFrame;

import arch.SweetHome3DMain.SweetHome3DMainImp;

public interface MainToMacConfig {
  public void bindToApplicationMenu(final SweetHome3DMainImp homeApplication);
  public boolean isWindowFullScreen(final JFrame frame);
}
