using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Windows.Media.Animation;
using System.IO;
using System.Windows.Threading;
using System.Windows.Forms;
using System.Collections.ObjectModel;
using Xceed.Wpf.Toolkit;

namespace ProjectionTest {
    public partial class MainWindow : Window {

        private Rect fullScreenDetection;
        private DoubleAnimation fadein = new DoubleAnimation(0, 100, TimeSpan.FromSeconds(120));
        private DoubleAnimation fadein2 = new DoubleAnimation(0, 100, TimeSpan.FromSeconds(60));
        private DoubleAnimation fadeout = new DoubleAnimation(100, 0, TimeSpan.FromSeconds(60));
        private double radius = 50;
        private SolidColorBrush actualBrush = new SolidColorBrush(Colors.White);
        private ObservableCollection<ColorItem> palette;
        private List<Key> pressedKeys = new List<Key>();
        private Shape cursor;
        private List<System.Windows.Controls.MenuItem> colorsMenus = new List<System.Windows.Controls.MenuItem>();
        private List<StackPanel> binders = new List<StackPanel>();

        private int selectedBinderIndex {
            get { return selectedBinderIndex; }
            set {
                try{
                    if (imagefullscreen)
                        if (value == 0)
                            value++; ;
                    StackPanel temp = (StackPanel)Binder.Children[value];
                    temp.Background = defaultColorBrush;
                    StackPanel old = (StackPanel)Binder.Children[selectedBinderIndex];
                    old.Background = new SolidColorBrush(Colors.White);

                    if (!fullscreen)
                        FullscreenButton_Click(null, null);
                    Image img = (Image)temp.Children[0];
                    EventImage.Source = img.Source;
                    EventImage.Visibility = Visibility.Visible;
                    imagefullscreen = true;

                    this.selectedBinderIndex = value;
                } catch {

                }
            }
        }
        private bool shapeSelected = true;
        private bool fullscreen;
        private bool imagefullscreen = false;
        private SolidColorBrush defaultColorBrush = new SolidColorBrush((Color)ColorConverter.ConvertFromString("#FFB4BDF5"));
        

        public MainWindow() {
            InitializeComponent();

            Console.WriteLine("Binder: " + Binder.Children.ToString());

            fullscreen = false;

            List<ColorItem> temp = new List<ColorItem>();

            FullScreenSizeLabel.Content = (int)EventsGrid.ActualWidth + "x" + (int)EventsGrid.ActualHeight;

            temp.Add(new ColorItem(Colors.Red, "Red"));
            temp.Add(new ColorItem(Colors.Black, "Black"));
            temp.Add(new ColorItem((Color)ColorConverter.ConvertFromString("#FFFFFF00"), "Neon Yellow"));
            temp.Add(new ColorItem((Color)ColorConverter.ConvertFromString("#FFFFEE00"), "Yellow"));
            temp.Add(new ColorItem((Color)ColorConverter.ConvertFromString("#FFAAEE00"), "Luminescent Green"));
            temp.Add(new ColorItem((Color)ColorConverter.ConvertFromString("#FF00FFEE"), "Poolish Blue"));
            temp.Add(new ColorItem((Color)ColorConverter.ConvertFromString("#FF0000FF"), "Strong Blue"));
            temp.Add(new ColorItem((Color)ColorConverter.ConvertFromString("#FFAA00FF"), "Purple"));
            temp.Add(new ColorItem((Color)ColorConverter.ConvertFromString("#FF6600FF"), "Dark Purple"));
            temp.Add(new ColorItem((Color)ColorConverter.ConvertFromString("#FFFF00FF"), "Magenta"));

            palette = new ObservableCollection<ColorItem>(temp);

            ColorPicker.AvailableColors = palette;
            ColorPicker.AvailableColorsHeader = "Recommended Colors";
            FullscreenColorPicker.AvailableColors = palette;
            FullscreenColorPicker.AvailableColorsHeader = "Recommended Colors";

            System.Windows.Controls.MenuItem white = new System.Windows.Controls.MenuItem();
            white.Header = "White";
            white.Click += ColorSelected;
            Rectangle whiteIcon = new Rectangle();
            whiteIcon.Fill = new SolidColorBrush(Colors.White);
            white.Icon = whiteIcon;
            white.IsCheckable = true;
            white.IsChecked = false;
            ColorsMenuItem.Items.Add(white);
            colorsMenus.Add(white);
            foreach (ColorItem c in temp) {
                System.Windows.Controls.MenuItem m = new System.Windows.Controls.MenuItem();
                m.Header = c.Name;
                m.Click += ColorSelected;
                m.IsCheckable = true;
                Rectangle Icon = new Rectangle();
                Icon.Fill = new SolidColorBrush((Color)c.Color);
                m.Icon = Icon;
                colorsMenus.Add(m);
                ColorsMenuItem.Items.Add(m);
            }
        }

        private void EventsGrid_MouseMove(object sender, System.Windows.Input.MouseEventArgs e) {
            if (EventsGrid.Children.Contains(cursor)) EventsGrid.Children.Remove(cursor);

            if (shapeSelected) cursor = new Ellipse();
            else cursor = new Rectangle();

            if (cursor.Width != radius * 2) {
                cursor.Width = radius * 2;
                cursor.Height = radius * 2;
                cursor.Stroke = Brushes.LightGray;
                cursor.StrokeThickness = 2;
            }

            cursor.Margin = new Thickness(e.GetPosition(EventsGrid).X - radius, e.GetPosition(EventsGrid).Y - radius, 0, 0);

            if (FullscreenButtons.IsChecked) {
                var relativePoint = MouseDetectionGrid.TransformToAncestor(MainWindowView)
                                  .Transform(new Point(0, 0));
                fullScreenDetection = new Rect(relativePoint.X, relativePoint.Y, MouseDetectionGrid.ActualWidth, MouseDetectionGrid.ActualHeight);

                if (fullScreenDetection.Contains(e.GetPosition(MainWindowView))) {
                    if (FullscreenButton.Opacity <= 0 || FullscreenButton.Visibility == Visibility.Hidden) {
                        TurnOnControl(FullscreenButton);
                        if (fullscreen) {
                            System.Windows.Controls.Control[] controls = {  FullscreenClearButton, FullscreenColorPicker, FullscreenSaveButton,
                                                                            FullscreenSizeSlider, FullscreenUndoButton, FullscreenLabel, FullScreenSizeLabel };
                            TurnOnControl(controls);
                        }
                    }
                } else {
                    TurnOffAllFullscreenControls();
                }
            } else {
                TurnOffAllFullscreenControls();
            }

            EventsGrid.Children.Add(cursor);
        }

        private void TurnOnControl(System.Windows.Controls.Control c) {
            c.Visibility = Visibility.Visible;
            c.BeginAnimation(Image.OpacityProperty, fadein2);
            c.Opacity = 100;
        }
        private void TurnOnControl(System.Windows.Controls.Control[] controls) {
            foreach (System.Windows.Controls.Control c in controls) {
                c.Visibility = Visibility.Visible;
                c.BeginAnimation(Image.OpacityProperty, fadein2);
                c.Opacity = 100;
            }
        }
        private void TurnOffAllFullscreenControls() {
            System.Windows.Controls.Control[] controls = {  FullscreenButton, FullscreenClearButton, FullscreenColorPicker, FullscreenSaveButton,
                                                            FullscreenSizeSlider, FullscreenUndoButton, FullscreenLabel, FullScreenSizeLabel };
            foreach (System.Windows.Controls.Control c in controls) { c.Visibility = Visibility.Hidden; }
        }

        private void EventsGrid_MouseLeftButtonUp(object sender, MouseButtonEventArgs e) {
            Shape temp;
            if (shapeSelected) temp = new Ellipse();
            else temp = new Rectangle();
            temp.Width = radius * 2;
            temp.Height = radius * 2;
            temp.Margin = new Thickness(e.GetPosition(EventsGrid).X - radius, e.GetPosition(EventsGrid).Y - radius, 0, 0);
            temp.Fill = actualBrush;
            temp.Opacity = 0;
            temp.BeginAnimation(Image.OpacityProperty, fadein);
            EventsGrid.Children.Add(temp);
        }

        private void DragRectangle_MouseDown(object sender, MouseButtonEventArgs e) {
            if (e.ChangedButton == MouseButton.Left) {
                this.DragMove();
            }
        }

        private void CloseButton_Click(object sender, RoutedEventArgs e) {
            this.Close();
        }

        private void MainWindowView_KeyDown(object sender, System.Windows.Input.KeyEventArgs e) {
            if (pressedKeys.Contains(e.Key))
                return;
//            if (pressedKeys.Count > 2)
//                pressedKeys.Clear();
            pressedKeys.Add(e.Key);
            KeyCommands(sender, e);
            e.Handled = true;
        }

        private void MainWindowView_KeyUp(object sender, System.Windows.Input.KeyEventArgs e) {
            pressedKeys.Remove(e.Key);
            KeyCommands(sender, e);
            e.Handled = true;
        }

        private void KeyCommands(object sender, System.Windows.Input.KeyEventArgs e) {
            if(Binder.Children.Count > 1){
                if (pressedKeys.Contains(Key.Left)) {
                    selectedBinderIndex--;
                } else if (pressedKeys.Contains(Key.Right)) {
                    selectedBinderIndex++;
                }
            }
            if (pressedKeys.Contains(Key.Space)) ClearButton_Click(sender, e);
            else if (pressedKeys.Contains(Key.OemPlus)) radius += 25;
            else if (pressedKeys.Contains(Key.OemMinus)) { if (radius > 25) radius -= 25; }
            else if (pressedKeys.Contains(Key.Escape)) FullscreenButton_Click(sender, e);
            else if ((pressedKeys.Contains(Key.LeftCtrl) && pressedKeys.Contains(Key.Z)) ||
                        (pressedKeys.Contains(Key.RightCtrl) && pressedKeys.Contains(Key.Z)))
                UndoButton_Click(sender, e);
            else if ((pressedKeys.Contains(Key.LeftCtrl) && pressedKeys.Contains(Key.S)) ||
                        (pressedKeys.Contains(Key.RightCtrl) && pressedKeys.Contains(Key.S)))
                SaveButton_Click(sender, e);
            else if ((pressedKeys.Contains(Key.LeftAlt) || pressedKeys.Contains(Key.RightAlt)) && pressedKeys.Contains(Key.Enter))
                FullscreenButton_Click(sender, e);
        }

        private void ClearButton_Click(object sender, RoutedEventArgs e) {
            this.EventsGrid.Children.Clear();
        }
        private void UndoButton_Click(object sender, RoutedEventArgs e) {
            if (EventsGrid.Children.Count > 0) EventsGrid.Children.RemoveAt(EventsGrid.Children.Count - 1);
        }

        private void CreateSaveBitmap(Canvas canvas, string filename) {
            RenderTargetBitmap renderBitmap = new RenderTargetBitmap(
             (int)canvas.ActualWidth, (int)canvas.ActualHeight,
             96d, 96d, PixelFormats.Default);
            canvas.Measure(new Size(canvas.ActualWidth, canvas.ActualHeight));
            renderBitmap.Render(canvas);

            var encoder = new PngBitmapEncoder();
            encoder.Frames.Add(BitmapFrame.Create(renderBitmap));

            using (var file = File.Create(filename)) {
                encoder.Save(file);
            }
        }

        private void SaveButton_Click(object sender, RoutedEventArgs e) {

            var dialog = new System.Windows.Forms.SaveFileDialog();
            dialog.Filter = "Image File | *.png";
            dialog.DefaultExt = "png";
            dialog.Title = "Save Image as:";
            System.Windows.Forms.DialogResult result = dialog.ShowDialog();
            try {
                if (result == System.Windows.Forms.DialogResult.OK) {
                    CreateSaveBitmap(EventsGrid, dialog.FileName);
                    System.Windows.MessageBox.Show("Image successfully created", "", MessageBoxButton.OK, MessageBoxImage.Information);
                }
            } catch {
                System.Windows.MessageBox.Show("Image couldn't be created", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }

        private void EventsGrid_MouseLeave(object sender, System.Windows.Input.MouseEventArgs e) {
            EventsGrid.Children.Remove(cursor);
        }

        private void MainWindowView_SizeChanged(object sender, SizeChangedEventArgs e) {
            SizeInfoLabel.Content = (int)EventsGrid.ActualWidth + "x" + (int)EventsGrid.ActualHeight;
            FullScreenSizeLabel.Content = (int)EventsGrid.ActualWidth + "x" + ((int)EventsGrid.ActualHeight + (int)60);
            //EventImage.Width = EventsGrid.ActualWidth;
            //EventImage.Height = EventsGrid.ActualHeight + 60;
        }

        private void SizeSlider_ValueChanged(object sender, RoutedPropertyChangedEventArgs<double> e) {
            if ((int)e.NewValue > 45 && (int)e.NewValue < 55) s50MenuItem_Click(new object(), new RoutedEventArgs());
            else if ((int)e.NewValue > 95 && (int)e.NewValue < 105) s100MenuItem_Click(new object(), new RoutedEventArgs());
            else if ((int)e.NewValue > 145 && (int)e.NewValue < 155) s150MenuItem_Click(new object(), new RoutedEventArgs());
            else if ((int)e.NewValue > 195 && (int)e.NewValue < 205) s200MenuItem_Click(new object(), new RoutedEventArgs());
            radius = e.NewValue;
            e.Handled = true;
        }

        private void Maximize_Click(object sender, RoutedEventArgs e) {
            this.MaxHeight = SystemParameters.MaximizedPrimaryScreenHeight;
            this.WindowState = System.Windows.WindowState.Maximized;
            this.MaxHeight = double.PositiveInfinity;
            this.Maximize.Visibility = Visibility.Collapsed;
            this.Restore.Visibility = Visibility.Visible;
        }

        private void Restore_Click(object sender, RoutedEventArgs e) {
            this.WindowState = System.Windows.WindowState.Normal;
            this.Restore.Visibility = Visibility.Collapsed;
            this.Maximize.Visibility = Visibility.Visible;
        }

        private void MinimizeButton_Click(object sender, RoutedEventArgs e) {
            this.WindowState = System.Windows.WindowState.Minimized;
        }

        private void FullscreenButton_Click(object sender, RoutedEventArgs e) {
            if (!fullscreen) {
                this.EventsGrid.Margin = new Thickness(0, 0, 0, 0);
                this.WindowState = System.Windows.WindowState.Maximized;
                FullscreenButton.Margin = new Thickness(0, 0, 15, 95);
                BitmapImage i = new BitmapImage();
                i.BeginInit();
                i.UriSource = new Uri(@"pack://application:,,,/ProjectionTest;component/Images/fullscreenout.png");
                i.EndInit();
                this.FullScreenImage.Source = i;
                this.Topmost = true;
                fullscreen = true;
                this.UpperDock.Visibility = Visibility.Collapsed;
                this.BottomDock.Visibility = Visibility.Collapsed;
                LeftDockGrid.Visibility = Visibility.Collapsed;
                Grid.SetColumn(RightDockGrid, 0);
                MainWindowView_SizeChanged(sender, null);
            } else {
                this.EventsGrid.Margin = new Thickness(10, 15, 10, 10);
                this.WindowState = System.Windows.WindowState.Normal;
                FullscreenButton.Margin = new Thickness(0, 0, 15, 65);
                BitmapImage i = new BitmapImage();
                i.BeginInit();
                i.UriSource = new Uri(@"pack://application:,,,/ProjectionTest;component/Images/fullscreenin.png");
                i.EndInit();
                this.FullScreenImage.Source = i;
                this.Topmost = false;
                fullscreen = false;
                this.UpperDock.Visibility = Visibility.Visible;
                this.BottomDock.Visibility = Visibility.Visible;
                LeftDockGrid.Visibility = Visibility.Visible;
                Grid.SetColumn(RightDockGrid, 3);
                TurnOffAllFullscreenControls();
                EventImage.Visibility = Visibility.Collapsed;
            }
        }

        private void ColorPicker_SelectedColorChanged(object sender, RoutedPropertyChangedEventArgs<Color?> e) {
            actualBrush = new SolidColorBrush(e.NewValue.Value);
            Rectangle temp = new Rectangle();
            foreach (System.Windows.Controls.MenuItem m in colorsMenus) {
                temp = (Rectangle)m.Icon;
                if (temp.Fill.ToString() != actualBrush.ToString()) m.IsChecked = false;
                else m.IsChecked = true;
            }
        }

        private void CircleMenuItem_Click(object sender, RoutedEventArgs e) {
            CircleMenuItem.IsChecked = true;
            SquareMenuItem.IsChecked = false;
            shapeSelected = true;
        }
        private void SquareMenuItem_Click(object sender, RoutedEventArgs e) {
            CircleMenuItem.IsChecked = false;
            SquareMenuItem.IsChecked = true;
            shapeSelected = false;
        }
        private void s50MenuItem_Click(object sender, RoutedEventArgs e) {
            s50MenuItem.IsChecked = true;
            s100MenuItem.IsChecked = false;
            s150MenuItem.IsChecked = false;
            s200MenuItem.IsChecked = false;
            radius = 50;
            SizeSlider.Value = 50;
        }
        private void s100MenuItem_Click(object sender, RoutedEventArgs e) {
            s50MenuItem.IsChecked = false;
            s100MenuItem.IsChecked = true;
            s150MenuItem.IsChecked = false;
            s200MenuItem.IsChecked = false;
            radius = 100;
            SizeSlider.Value = 100;
        }
        private void s150MenuItem_Click(object sender, RoutedEventArgs e) {
            s50MenuItem.IsChecked = false;
            s100MenuItem.IsChecked = false;
            s150MenuItem.IsChecked = true;
            s200MenuItem.IsChecked = false;
            radius = 150;
            SizeSlider.Value = 150;
        }
        private void s200MenuItem_Click(object sender, RoutedEventArgs e) {
            s50MenuItem.IsChecked = false;
            s100MenuItem.IsChecked = false;
            s150MenuItem.IsChecked = false;
            s200MenuItem.IsChecked = true;
            radius = 200;
            SizeSlider.Value = 200;
        }
        private void ColorSelected(object sender, RoutedEventArgs e) {
            System.Windows.Controls.MenuItem temp = (System.Windows.Controls.MenuItem)sender;
            Color color = new Color();
            foreach (ColorItem c in palette) {
                if (temp.Header.ToString() == c.Name) color = (Color)c.Color;
                else if (temp.Header.ToString() == "White") color = Colors.White;
            }
            actualBrush = new SolidColorBrush(color);
            ColorPicker.SelectedColor = actualBrush.Color;
            foreach (System.Windows.Controls.MenuItem m in colorsMenus) {
                if (m.Header != temp.Header) m.IsChecked = false;
                else m.IsChecked = true;
            }
        }

        private void CreateNewBinder() {
            OpenFileDialog oFD = new OpenFileDialog();
            oFD.Filter = "PNG Image File | *.png";
            oFD.DefaultExt = "png";
            oFD.Title = "Open File";
            DialogResult d = oFD.ShowDialog();
            if (d == System.Windows.Forms.DialogResult.OK) {
                if (oFD.FileNames.Count() <= 1) {
                    StackPanel temp = RawBinder(oFD.FileName, Binder.Children.Count);
                    Binder.Children.Add(temp);
                    binders.Add(temp);
                } 
            } else
                return;
            pressedKeys.Clear();
        }
        private void CreateNewBinder(int index) {
            OpenFileDialog oFD = new OpenFileDialog();
            oFD.Filter = "PNG Image File | *.png";
            oFD.DefaultExt = "png";
            oFD.Title = "Open File";
            DialogResult d = oFD.ShowDialog();
            if (d == System.Windows.Forms.DialogResult.OK) {
                if (oFD.FileNames.Count() <= 1) {
                    StackPanel temp = RawBinder(oFD.FileName, Binder.Children.Count);
                    Binder.Children.Add(temp);
                    binders.Insert(index, temp);
                }
            } else
                return;
            pressedKeys.Clear();
        }

        private StackPanel RawBinder(string path, int button) {
            StackPanel panel = new StackPanel() { Name = "NumPad" + button, Orientation = System.Windows.Controls.Orientation.Horizontal,
                                                  Margin = new Thickness(5, 5, 5, 0), HorizontalAlignment= System.Windows.HorizontalAlignment.Stretch,
                                                  VerticalAlignment = VerticalAlignment.Stretch};
            BitmapImage i = new BitmapImage();
            i.BeginInit();
            i.UriSource = new Uri(path);
            i.EndInit();
            Image image = new Image() { Source = i, Height = 100, Width= 153,
                Stretch = Stretch.Fill, HorizontalAlignment = System.Windows.HorizontalAlignment.Stretch };

            panel.Children.Add(image);
            panel.Children.Add(new System.Windows.Controls.Label() { Content = "Key Bind: " + panel.Name, FontSize = 16,
                VerticalContentAlignment =VerticalAlignment.Center });

            panel.MouseLeftButtonDown += panel_MouseLeftButtonDown;

            return panel;
        }

        void panel_MouseLeftButtonDown(object sender, MouseButtonEventArgs e) {
            if (!fullscreen)
                FullscreenButton_Click(null, null);
            StackPanel p = (StackPanel)sender;
            Image temp = (Image)p.Children[0];
            EventImage.Source = temp.Source;
            EventImage.Visibility = Visibility.Visible;
            selectedBinderIndex = Binder.Children.IndexOf(p);
            imagefullscreen = true;
        }

        private void RootBind_MouseDown(object sender, MouseButtonEventArgs e) {
            CreateNewBinder();
        }

        private void DragRectangle_Initialized(object sender, EventArgs e) {

            selectedBinderIndex = 0;
        }
    }
}

