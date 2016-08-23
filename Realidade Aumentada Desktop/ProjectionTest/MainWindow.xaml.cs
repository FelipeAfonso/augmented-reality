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
using System.Xml;
using Microsoft.VisualBasic;
using System.Threading;
using System.Net;

namespace ProjectionTest {
    public partial class MainWindow : Window {

        #region Variables
        private Rect fullScreenDetection;
        private DoubleAnimation fadein = new DoubleAnimation() {
            To = 100, From = 0, BeginTime = TimeSpan.FromSeconds(120), Duration = TimeSpan.FromSeconds(50),
            FillBehavior = FillBehavior.Stop
        };
        private DoubleAnimation fadein2 = new DoubleAnimation(0, 100, TimeSpan.FromSeconds(120));
        private DoubleAnimation fadeout = new DoubleAnimation(100, 0, TimeSpan.FromSeconds(120));
        private double radius = 50;
        private SolidColorBrush actualBrush = new SolidColorBrush(Colors.White);
        private ObservableCollection<ColorItem> palette;
        private List<Key> pressedKeys = new List<Key>();
        private Shape cursor;
        private List<System.Windows.Controls.MenuItem> colorsMenus = new List<System.Windows.Controls.MenuItem>();
        private List<StackPanel> binders = new List<StackPanel>();
        private int _selectedBinderIndex = 0;
        private int selectedBinderIndex {
            get { return _selectedBinderIndex; }
            set {
                /* Esse setter funciona como um event handler. 
                 * Ele é responsável por guiar a seleção do usuario 
                 * ao utilizar as setas para trocar de mapa
                 */

                try {
                    if (value == 0 && value == _selectedBinderIndex) {
                        _selectedBinderIndex = value;
                    } else {

                        int actualBinderIndex = value % Binder.Children.Count;
                        if (actualBinderIndex == 0) {
                            if (value > _selectedBinderIndex) actualBinderIndex++;
                            else actualBinderIndex--;
                        }
                        if (actualBinderIndex < 0) actualBinderIndex = Binder.Children.Count - 1;

                        StackPanel temp = (StackPanel)Binder.Children[actualBinderIndex];
                        temp.Background = defaultColorBrush;
                        StackPanel old = (StackPanel)Binder.Children[selectedBinderIndex];
                        old.Background = new SolidColorBrush(Colors.White);

                        if (!fullscreen)
                            FullscreenButton_Click(null, null);
                        if (temp.Children[0].GetType().ToString() == "System.Windows.Controls.Canvas") {
                            EventCanvas = (Canvas)temp.Children[0];
                            EventCanvas.Visibility = Visibility.Visible;
                        } else {
                            Image i = (Image)temp.Children[0];
                            EventImage.Source = i.Source;
                            EventImage.Visibility = Visibility.Visible;
                        }
                        imagefullscreen = true;
                        Console.WriteLine("value: " + value + " binder: " + actualBinderIndex);
                        _selectedBinderIndex = actualBinderIndex;
                    }
                } catch {

                }
            }
        }

        private int shapeSelected = 1;
        private bool fullscreen;
        private bool imagefullscreen = false;
        private bool texboxSelected = false;
        private FontFamily fontFamily;
        private float fontSize = 12;
        private System.Windows.Controls.TextBox selectedTB;
        private SolidColorBrush defaultColorBrush = new SolidColorBrush((Color)ColorConverter.ConvertFromString("#FFB4BDF5"));

        private System.Windows.WindowState previousWindowState;

        #endregion

        #region Construtor
        public MainWindow() {

            InitializeComponent();

            //if (System.Net.NetworkInformation.NetworkInterface.GetIsNetworkAvailable()) {
            ServerInfoLabel.Content = "Conectado";//TCPServer.GetLocalIPAddress(); //new WebClient().DownloadString("http://icanhazip.com");
            new Thread(thread).Start();
            //} else { ServerInfoLabel.Content = "Não há conexão com a internet"; }


            this.MaxHeight = SystemParameters.MaximizedPrimaryScreenHeight;

            fadein.Completed += (s, a) => EventImage.Opacity = 100;

            Console.WriteLine("Binder: " + Binder.Children.ToString());

            fullscreen = false;

            List<ColorItem> temp = new List<ColorItem>();

            FullScreenSizeLabel.Content = (int)EventsGrid.ActualWidth + "x" + (int)EventsGrid.ActualHeight;
            fontFamily = new FontFamily("Segoe UI");


            //Adicionando as devidas cores a paleta padrão da biblioteca Xceed

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


            //Populando o Menu Item com as cores pré-definidas (As mesmas da paleta)

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
                var m = new System.Windows.Controls.MenuItem();
                m.Header = c.Name;
                m.Click += ColorSelected;
                m.IsCheckable = true;
                Rectangle Icon = new Rectangle();
                Icon.Fill = new SolidColorBrush((Color)c.Color);
                Icon.Height = 16; Icon.Width = 16;
                m.Icon = Icon;
                colorsMenus.Add(m);
                ColorsMenuItem.Items.Add(m);
            }
        }
        #endregion

        #region Handlers

        #region Fullscreen Handlers

        private void FullscreenButton_Click(object sender, RoutedEventArgs e) {
            // Principal método para maximizar a tela 
            if (!fullscreen) {
                AnimateImage.Visibility = Visibility.Visible;
                this.MaxHeight = Double.PositiveInfinity;
                this.EventsGrid.Margin = new Thickness(0, 0, 0, 0);
                previousWindowState = this.WindowState;
                //Alerta, gambiarra abaixo
                if (this.WindowState == System.Windows.WindowState.Maximized) {
                    Restore_Click(null, null);
                }

                this.WindowState = System.Windows.WindowState.Maximized;
                FullscreenButton.Margin = new Thickness(0, 0, 15, 95);
                BitmapImage i = new BitmapImage();
                i.BeginInit();
                i.UriSource = new Uri(@"pack://application:,,,/ProjectionTest;component/Images/fullscreenout.png");
                i.EndInit();
                this.FullScreenImage.Source = i;
                // Se o programa estiver bugando, desative a opção abaixo
                this.Topmost = false;
                fullscreen = true;
                this.UpperDock.Visibility = Visibility.Collapsed;
                this.BottomDock.Visibility = Visibility.Collapsed;
                LeftDockGrid.Visibility = Visibility.Collapsed;
                Grid.SetColumn(RightDockGrid, 0);
                MainWindowView_SizeChanged(sender, null);
            } else {
                this.MaxHeight = SystemParameters.MaximizedPrimaryScreenHeight;
                imagefullscreen = false;
                this.EventsGrid.Margin = new Thickness(10, 15, 10, 10);
                this.WindowState = previousWindowState;
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
                EventCanvas.Visibility = Visibility.Collapsed;
                AnimateImage.Visibility = Visibility.Collapsed;
                this.Restore.Visibility = Visibility.Collapsed;
                this.Maximize.Visibility = Visibility.Visible;
            }
        }
        //Estes métodos facilitam sua vida ao maximizar a tela
        //foram necessários devido ao layout da página
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
        #endregion

        #region EventsGrid Handlers
        private void EventsGrid_MouseMove(object sender, System.Windows.Input.MouseEventArgs e) {
            //Lida com a aparencia do cursor e com a detecção da posição do mouse a fim de tornar a barra de ferramentas 
            //inferior (da tela) quando esta em tela cheia
            if (EventsGrid.Children.Contains(cursor)) EventsGrid.Children.Remove(cursor);
            if (shapeSelected == 1) { cursor = new Ellipse(); EventsGrid.Cursor = System.Windows.Input.Cursors.None; } else if (shapeSelected == 0) { cursor = new Rectangle(); EventsGrid.Cursor = System.Windows.Input.Cursors.None; } else {
                EventsGrid.Cursor = System.Windows.Input.Cursors.IBeam;
            }

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
            if (shapeSelected < 2) EventsGrid.Children.Add(cursor);
        }
        private void EventsGrid_MouseLeftButtonUp(object sender, MouseButtonEventArgs e) {
            if (texboxSelected) { selectedTB.MoveFocus(new TraversalRequest(FocusNavigationDirection.Previous)); Keyboard.ClearFocus(); texboxSelected = false; selectedTB = null; } else {
                if (shapeSelected == 2) {
                    System.Windows.Controls.TextBox input = new System.Windows.Controls.TextBox();
                    input.Background = null;
                    input.SelectionBrush = Brushes.White;
                    input.BorderBrush = null;
                    input.Foreground = actualBrush;
                    input.FontFamily = fontFamily;
                    input.FontSize = fontSize;
                    input.TextWrapping = TextWrapping.Wrap;
                    input.Margin = new Thickness(e.GetPosition(EventsGrid).X, e.GetPosition(EventsGrid).Y, 0, 0);
                    input.GotFocus += TextBoxSelected;
                    input.LostFocus += TextBoxUnselected;
                    EventsGrid.Children.Add(input);
                    this.Focus();
                    input.Focus();
                } else {
                    //Insere o circulo ou retangulo no canvas
                    Shape temp;
                    if (shapeSelected == 1) temp = new Ellipse();
                    else temp = new Rectangle();
                    temp.Width = radius * 2;
                    temp.Height = radius * 2;
                    temp.Margin = new Thickness(e.GetPosition(EventsGrid).X - radius, e.GetPosition(EventsGrid).Y - radius, 0, 0);
                    temp.Fill = actualBrush;
                    temp.Opacity = 0;
                    temp.BeginAnimation(Image.OpacityProperty, fadein2);
                    EventsGrid.Children.Add(temp);
                }
            }
        }
        private void EventsGrid_MouseLeftButtonDown(object sender, MouseButtonEventArgs e) {
        }

        private void EventsGrid_MouseLeave(object sender, System.Windows.Input.MouseEventArgs e) {
            //Remove o cursor customizado quando o mouse sai do Canvas
            EventsGrid.Children.Remove(cursor);
        }
        private void TextBoxSelected(object sender, RoutedEventArgs e) {
            selectedTB = (System.Windows.Controls.TextBox)sender;
            texboxSelected = true;
        }
        private void TextBoxUnselected(object sender, RoutedEventArgs e) {
            selectedTB = null;
            texboxSelected = false;
        }
        #endregion

        #region KeyHandling
        private void MainWindowView_KeyDown(object sender, System.Windows.Input.KeyEventArgs e) {
            //Insere a tecla pressionada na lista de tecla pressionadas e chama o método de verificação
            if (!texboxSelected) {
                if (pressedKeys.Contains(e.Key))
                    return;
                //            if (pressedKeys.Count > 2)
                //                pressedKeys.Clear();
                pressedKeys.Add(e.Key);
                KeyCommands(sender, e);
                e.Handled = true;
            }
        }

        private void MainWindowView_KeyUp(object sender, System.Windows.Input.KeyEventArgs e) {
            //Remove a tecla pressionada na lista de tecla pressionadas e chama o método de verificação
            if (!texboxSelected) {
                pressedKeys.Remove(e.Key);
                KeyCommands(sender, e);
                e.Handled = true;
            }
        }

        private void KeyCommands(object sender, System.Windows.Input.KeyEventArgs e) {
            //Confere as teclas pressionadas para designar a função de cada uma, caso elas tenham sido pressionadas
            if (Binder.Children.Count > 1) {
                if (pressedKeys.Contains(Key.Left)) {
                    BinderImageAnimate();
                    selectedBinderIndex--;
                } else if (pressedKeys.Contains(Key.Right)) {
                    BinderImageAnimate();
                    selectedBinderIndex++;
                }
            }
            if (pressedKeys.Contains(Key.Space)) ClearButton_Click(sender, e);
            else if (pressedKeys.Contains(Key.OemPlus)) radius += 25;
            else if (pressedKeys.Contains(Key.OemMinus)) { if (radius > 25) radius -= 25; } else if (pressedKeys.Contains(Key.Enter)) {
                GenericEvent();
            } else if (pressedKeys.Contains(Key.Escape)) FullscreenButton_Click(sender, e);
            else if ((pressedKeys.Contains(Key.LeftCtrl) && pressedKeys.Contains(Key.Z)) ||
                        (pressedKeys.Contains(Key.RightCtrl) && pressedKeys.Contains(Key.Z)))
                UndoButton_Click(sender, e);
            else if ((pressedKeys.Contains(Key.LeftCtrl) && pressedKeys.Contains(Key.S)) ||
                        (pressedKeys.Contains(Key.RightCtrl) && pressedKeys.Contains(Key.S)))
                SaveButton_Click(sender, e);
            else if ((pressedKeys.Contains(Key.LeftAlt) || pressedKeys.Contains(Key.RightAlt)) && pressedKeys.Contains(Key.Enter))
                FullscreenButton_Click(sender, e);
        }

        private void GenericEvent() {
            if (imagefullscreen) {
                AnimateImage.Fill = Brushes.White;
                DoubleAnimation anim = new DoubleAnimation(0, 100, TimeSpan.FromSeconds(120));
                anim.Completed += (s, a) => AnimateImage.Fill = Brushes.Black;
                EventImage.BeginAnimation(Image.OpacityProperty, anim);
            }
        }

        private void MainWindowView_TextInput(object sender, TextCompositionEventArgs e) {
            if (texboxSelected) {
                Char keyChar = (Char)System.Text.Encoding.ASCII.GetBytes(e.Text)[0];
                selectedTB.Text += keyChar;
                selectedTB.Width += 10;
            }
        }
        #endregion

        #region MainButtons Handlers
        private void ClearButton_Click(object sender, RoutedEventArgs e) {
            //Limpa o canvas
            this.EventsGrid.Children.Clear();
        }
        private void UndoButton_Click(object sender, RoutedEventArgs e) {
            //Retira o ultimo controle inserido no canvas
            if (EventsGrid.Children.Count > 0) EventsGrid.Children.RemoveAt(EventsGrid.Children.Count - 1);
        }
        private void SaveButton_Click(object sender, RoutedEventArgs e) {
            //Lança o SaveFileDialog para salvar a imagem 
            var dialog = new System.Windows.Forms.SaveFileDialog();
            dialog.Filter = "Image File (.png)| *.png|Augmented Reality Map (.arm)|*.arm";
            dialog.DefaultExt = "png";
            dialog.Title = "Save Image as:";
            System.Windows.Forms.DialogResult result = dialog.ShowDialog();
            try {
                if (result == System.Windows.Forms.DialogResult.OK) {
                    if (dialog.FileName.Substring(dialog.FileName.Length - 4).Equals(".png")) {
                        CreateSaveBitmap(EventsGrid, dialog.FileName);
                        System.Windows.MessageBox.Show("Image successfully created", "", MessageBoxButton.OK, MessageBoxImage.Information);
                    } else if (dialog.FileName.Substring(dialog.FileName.Length - 4).Equals(".arm")) {
                        using (XmlWriter writer = XmlWriter.Create(dialog.FileName, new XmlWriterSettings() { Indent = true })) {
                            writer.WriteStartDocument();
                            writer.WriteStartElement("Map");

                            writer.WriteElementString("Name", Interaction.InputBox("Map Name", "Save Image as: ",
                                dialog.FileName.Substring(dialog.FileName.LastIndexOf('\\') + 1)
                                .Substring(0, dialog.FileName.Substring(dialog.FileName.LastIndexOf('\\') + 1).Length - 4)));

                            writer.WriteElementString("Created_Date", DateTime.Now.ToString());
                            writer.WriteStartElement("Controls");
                            foreach (FrameworkElement c in EventsGrid.Children) {
                                writer.WriteStartElement("Control");
                                writer.WriteElementString("Type", c.GetType().ToString());
                                if (c.GetType().ToString() == "System.Windows.Controls.TextBox") {
                                    System.Windows.Controls.TextBox tb = (System.Windows.Controls.TextBox)c;
                                    writer.WriteElementString("Content", tb.Text);
                                    writer.WriteElementString("Color", tb.Foreground.ToString());
                                    writer.WriteElementString("MarginLeft", tb.Margin.Left.ToString());
                                    writer.WriteElementString("MarginTop", tb.Margin.Top.ToString());
                                    writer.WriteElementString("FontSize", tb.FontSize.ToString());
                                    writer.WriteElementString("FontFamily", tb.FontFamily.ToString());
                                } else {
                                    Shape shape = (Shape)c;
                                    writer.WriteElementString("Radius", Convert.ToString(shape.Width / 2));
                                    writer.WriteElementString("MarginLeft", shape.Margin.Left.ToString());
                                    writer.WriteElementString("MarginTop", shape.Margin.Top.ToString());
                                    writer.WriteElementString("Fill", shape.Fill.ToString());
                                }
                                writer.WriteEndElement();
                            }
                            writer.WriteEndElement();
                            writer.WriteEndElement();
                            writer.WriteEndDocument();
                        }
                    }

                }
            } catch {
                System.Windows.MessageBox.Show("Map couldn't be created", "Error", MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }
        private void CreateSaveBitmap(Canvas canvas, string filename) {
            //Renderiza o Canvas e salva o arquivo no local especificado
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
        private void SizeSlider_ValueChanged(object sender, RoutedPropertyChangedEventArgs<double> e) {
            //Ocorre toda vez que o valor do Raio é alterado e lança métodos para alterar o tamanho selecionado nos menu itens
            if ((int)e.NewValue > 45 && (int)e.NewValue < 55) s50MenuItem_Click(new object(), new RoutedEventArgs());
            else if ((int)e.NewValue > 95 && (int)e.NewValue < 105) s100MenuItem_Click(new object(), new RoutedEventArgs());
            else if ((int)e.NewValue > 145 && (int)e.NewValue < 155) s150MenuItem_Click(new object(), new RoutedEventArgs());
            else if ((int)e.NewValue > 195 && (int)e.NewValue < 205) s200MenuItem_Click(new object(), new RoutedEventArgs());
            radius = e.NewValue;
            e.Handled = true;
        }
        private void ColorPicker_SelectedColorChanged(object sender, RoutedPropertyChangedEventArgs<Color?> e) {
            //Handler do ColorPicker, para alterar a cor do controle a ser inserido
            actualBrush = new SolidColorBrush(e.NewValue.Value);
            Rectangle temp = new Rectangle();
            foreach (System.Windows.Controls.MenuItem m in colorsMenus) {
                temp = (Rectangle)m.Icon;
                if (temp.Fill.ToString() != actualBrush.ToString()) m.IsChecked = false;
                else m.IsChecked = true;
            }
        }
        private void ColorSelected(object sender, RoutedEventArgs e) {
            //Também altera a cor do controle a ser inserido, porém este é um handler genérico
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
        #endregion

        #region WindowButtons Handlers
        //Métodos genéricos utilizados devido a customização dos botões padrão do windows
        private void MainWindowView_SizeChanged(object sender, SizeChangedEventArgs e) {
            SizeInfoLabel.Content = (int)EventsGrid.ActualWidth + "x" + (int)EventsGrid.ActualHeight;
            FullScreenSizeLabel.Content = (int)EventsGrid.ActualWidth + "x" + ((int)EventsGrid.ActualHeight + (int)60);
            //EventImage.Width = EventsGrid.ActualWidth;
            //EventImage.Height = EventsGrid.ActualHeight + 60;
        }
        private void DragRectangle_MouseDown(object sender, MouseButtonEventArgs e) {
            if (e.ChangedButton == MouseButton.Left) {
                this.DragMove();
            }
        }
        private void CloseButton_Click(object sender, RoutedEventArgs e) {
            this.Close();
        }
        private void Maximize_Click(object sender, RoutedEventArgs e) {
            this.MaxHeight = SystemParameters.MaximizedPrimaryScreenHeight;
            this.WindowState = System.Windows.WindowState.Maximized;
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
        #endregion

        #region MenuItem Handlers
        //Handlers para os MenuItens do ContextMenu
        private void CircleMenuItem_Click(object sender, RoutedEventArgs e) {
            if (texboxSelected) { selectedTB.MoveFocus(new TraversalRequest(FocusNavigationDirection.Previous)); Keyboard.ClearFocus(); texboxSelected = false; selectedTB = null; }
            CircleMenuItem.IsChecked = true;
            SquareMenuItem.IsChecked = false;
            TextMenuItem.IsChecked = false;
            shapeSelected = 1;
        }
        private void SquareMenuItem_Click(object sender, RoutedEventArgs e) {
            if (texboxSelected) { selectedTB.MoveFocus(new TraversalRequest(FocusNavigationDirection.Previous)); Keyboard.ClearFocus(); texboxSelected = false; selectedTB = null; }
            CircleMenuItem.IsChecked = false;
            SquareMenuItem.IsChecked = true;
            TextMenuItem.IsChecked = false;
            shapeSelected = 0;
        }
        private void TextMenuItem_Click(object sender, RoutedEventArgs e) {
            if (texboxSelected) { selectedTB.MoveFocus(new TraversalRequest(FocusNavigationDirection.Previous)); Keyboard.ClearFocus(); texboxSelected = false; selectedTB = null; }
            CircleMenuItem.IsChecked = false;
            SquareMenuItem.IsChecked = false;
            TextMenuItem.IsChecked = true;
            shapeSelected = 2;
        }
        private void s50MenuItem_Click(object sender, RoutedEventArgs e) {
            if (texboxSelected) { selectedTB.MoveFocus(new TraversalRequest(FocusNavigationDirection.Previous)); Keyboard.ClearFocus(); texboxSelected = false; selectedTB = null; }
            s50MenuItem.IsChecked = true;
            s100MenuItem.IsChecked = false;
            s150MenuItem.IsChecked = false;
            s200MenuItem.IsChecked = false;
            radius = 50;
            SizeSlider.Value = 50;
        }
        private void s100MenuItem_Click(object sender, RoutedEventArgs e) {
            if (texboxSelected) { selectedTB.MoveFocus(new TraversalRequest(FocusNavigationDirection.Previous)); Keyboard.ClearFocus(); texboxSelected = false; selectedTB = null; }
            s50MenuItem.IsChecked = false;
            s100MenuItem.IsChecked = true;
            s150MenuItem.IsChecked = false;
            s200MenuItem.IsChecked = false;
            radius = 100;
            SizeSlider.Value = 100;
        }
        private void s150MenuItem_Click(object sender, RoutedEventArgs e) {
            if (texboxSelected) { selectedTB.MoveFocus(new TraversalRequest(FocusNavigationDirection.Previous)); Keyboard.ClearFocus(); texboxSelected = false; selectedTB = null; }
            s50MenuItem.IsChecked = false;
            s100MenuItem.IsChecked = false;
            s150MenuItem.IsChecked = true;
            s200MenuItem.IsChecked = false;
            radius = 150;
            SizeSlider.Value = 150;
        }
        private void s200MenuItem_Click(object sender, RoutedEventArgs e) {
            if (texboxSelected) { selectedTB.MoveFocus(new TraversalRequest(FocusNavigationDirection.Previous)); Keyboard.ClearFocus(); texboxSelected = false; selectedTB = null; }
            s50MenuItem.IsChecked = false;
            s100MenuItem.IsChecked = false;
            s150MenuItem.IsChecked = false;
            s200MenuItem.IsChecked = true;
            radius = 200;
            SizeSlider.Value = 200;
        }
        private void FontSizeMenuItem_Click(object sender, RoutedEventArgs e) {
            FontDialog fontDialog = new FontDialog();
            fontDialog.AllowVectorFonts = true;
            fontDialog.AllowVerticalFonts = true;
            DialogResult result = fontDialog.ShowDialog();
            if (result == System.Windows.Forms.DialogResult.OK) {
                System.Drawing.Font font = fontDialog.Font;
                fontFamily = new FontFamily(font.FontFamily.Name);
                fontSize = font.Size;
            }
        }
        private void Import_Click(object sender, RoutedEventArgs e) {
            OpenFileDialog oFD = new OpenFileDialog();
            oFD.Filter = "PNG Image File (.png)| *.png";
            oFD.DefaultExt = "png";
            oFD.Title = "Import File";
            DialogResult d = oFD.ShowDialog();
            if (d == System.Windows.Forms.DialogResult.OK) {
                try {
                    BitmapImage i = new BitmapImage();
                    i.BeginInit();
                    i.UriSource = new Uri(oFD.FileName);
                    i.EndInit();
                    var img = new Image() {
                        Source = i, Stretch = Stretch.Fill
                    };
                    EventsGrid.Children.Add(img);
                } catch { }
            }
        }
        #endregion

        #region Binder Management
        private void CreateNewBinder() {
            //Seleciona a imagem padrão do novo StackPanel que será o Binder (ainda vou alterar esse nome)
            OpenFileDialog oFD = new OpenFileDialog() { Multiselect = true };
            oFD.Filter = "PNG Image File (.png)| *.png|Augmented Reality Map (.arm)| *.arm";
            oFD.DefaultExt = "png";
            oFD.Title = "Open File";
            DialogResult d = oFD.ShowDialog();
            if (d == System.Windows.Forms.DialogResult.OK) {
                foreach (string f in oFD.FileNames) {
                    if (f.Substring(f.Length - 4) == ".png") {
                        StackPanel temp = RawBinder(f, Binder.Children.Count);
                        Binder.Children.Add(temp);
                        binders.Add(temp);
                    } else if (f.Substring(f.Length - 4) == ".arm") {
                        StackPanel temp = ArmBinder(f, Binder.Children.Count);
                        Binder.Children.Add(temp);
                        binders.Add(temp);
                    }
                }
            } else
                return;
            pressedKeys.Clear();
        }
        private void CreateNewBinder(int index) {
            //Override para inserir o novo binder em uma posição especifica (ainda será utilizado)
            OpenFileDialog oFD = new OpenFileDialog() { Multiselect = true };
            oFD.Filter = "PNG Image File | *.png";
            oFD.DefaultExt = "png";
            oFD.Title = "Open File";
            DialogResult d = oFD.ShowDialog();
            if (d == System.Windows.Forms.DialogResult.OK) {
                if (oFD.FileNames.Count() <= 1) {
                    StackPanel temp = RawBinder(oFD.FileName, Binder.Children.Count);
                    Binder.Children.Add(temp);
                    binders.Insert(index, temp);
                } else {
                    foreach (string f in oFD.FileNames) {
                        StackPanel temp = RawBinder(f, Binder.Children.Count);
                        Binder.Children.Add(temp);
                        binders.Insert(index, temp);
                    }
                }
            } else
                return;
            pressedKeys.Clear();
        }
        private StackPanel RawBinder(string path, int button) {
            //Retorna um Binder com os padrões necessários
            StackPanel panel = new StackPanel() {
                Name = "NumPad" + button, Orientation = System.Windows.Controls.Orientation.Horizontal,
                Margin = new Thickness(5, 5, 5, 0), HorizontalAlignment = System.Windows.HorizontalAlignment.Stretch,
                VerticalAlignment = VerticalAlignment.Stretch
            };
            BitmapImage i = new BitmapImage();
            i.BeginInit();
            i.UriSource = new Uri(path);
            i.EndInit();
            Image image = new Image() {
                Source = i, Height = 100, Width = 153,
                Stretch = Stretch.Fill, HorizontalAlignment = System.Windows.HorizontalAlignment.Stretch,
                Margin = new Thickness(5, 5, 5, 5)
            };

            panel.Children.Add(image);
            panel.Children.Add(new System.Windows.Controls.Label() {
                Content = path.Substring(path.LastIndexOf('\\') + 1)
                                                                    .Substring(0, path.Substring(path.LastIndexOf('\\') + 1).Length - 4)
                                                                    , FontSize = 16
                                                                    , VerticalContentAlignment = VerticalAlignment.Center
            });
            panel.MouseLeftButtonDown += panel_MouseLeftButtonDown;
            var delete_panel = new System.Windows.Controls.MenuItem();
            var separator = new Separator();

            panel.MouseRightButtonDown += (s, a) => {
                delete_panel.Header = "Delete Binder";
                delete_panel.Tag = panel;
                delete_panel.Click += delete_panel_Click;
                this.ContextMenu.Items.Insert(0, delete_panel);
            };
            this.ContextMenu.Closed += (s, a) => { this.ContextMenu.Items.Remove(delete_panel); };
            return panel;
        }
        private StackPanel ArmBinder(string path, int button) {
            StackPanel panel = new StackPanel() {
                Name = "NumPad" + button, Orientation = System.Windows.Controls.Orientation.Horizontal,
                Margin = new Thickness(5, 5, 5, 0), HorizontalAlignment = System.Windows.HorizontalAlignment.Stretch,
                VerticalAlignment = VerticalAlignment.Stretch
            };

            var map = new ARMap(path);
            Canvas canvas = new Canvas() { Background = Brushes.Black };
            foreach (FrameworkElement e in map.Elements) { canvas.Children.Add(e); }

            panel.Children.Add(canvas);
            panel.Children.Add(new System.Windows.Controls.Label() {
                Content = path.Substring(path.LastIndexOf('\\') + 1)
                    .Substring(0, path.Substring(path.LastIndexOf('\\') + 1).Length - 4)
                    , FontSize = 16
                    , VerticalContentAlignment = VerticalAlignment.Center
            });

            panel.MouseLeftButtonDown += panel_MouseLeftButtonDown;
            var delete_panel = new System.Windows.Controls.MenuItem();
            var separator = new Separator();
            panel.MouseRightButtonDown += (s, a) => {
                delete_panel.Header = "Delete Binder";
                delete_panel.Tag = panel;
                delete_panel.Click += delete_panel_Click;
                this.ContextMenu.Items.Insert(0, delete_panel);
            };
            this.ContextMenu.Closed += (s, a) => { this.ContextMenu.Items.Remove(delete_panel); };

            return panel;
        }
        void delete_panel_Click(object sender, RoutedEventArgs e) {
            var m = (System.Windows.Controls.MenuItem)sender;
            binders.Remove((StackPanel)m.Tag);
            Binder.Children.Remove((StackPanel)m.Tag);
        }
        private void panel_MouseLeftButtonDown(object sender, MouseButtonEventArgs e) {
            //Handler genérico para os binder gerados programáticamente
            if (!fullscreen)
                FullscreenButton_Click(null, null);
            StackPanel p = (StackPanel)sender;
            if (p.Children[0].GetType().ToString() == "System.Windows.Controls.Canvas") {
                Canvas tempCanvas = (Canvas)p.Children[0];
                foreach (var element in tempCanvas.Children) {
                    if (element.GetType().ToString() == "System.Windows.Shapes.Ellipse") {
                        Ellipse temp = (Ellipse)element;
                        EventCanvas.Children.Add(new Ellipse() {
                            Width = temp.Width, Height = temp.Height,
                            Margin = temp.Margin, Fill = temp.Fill
                        });
                    } else if (element.GetType().ToString() == "System.Windows.Shapes.Rectangle") {
                        Rectangle temp = (Rectangle)element;
                        EventCanvas.Children.Add(new Ellipse() {
                            Width = temp.Width, Height = temp.Height,
                            Margin = temp.Margin, Fill = temp.Fill
                        });
                    } else {

                    }
                }
            } else {
                Image temp = (Image)p.Children[0];
                EventImage.Source = temp.Source;
                EventImage.Visibility = Visibility.Visible;
                selectedBinderIndex = Binder.Children.IndexOf(p);
            }
            imagefullscreen = true;
        }
        private void RootBind_MouseDown(object sender, MouseButtonEventArgs e) {
            //Handler do primeiro Binder
            CreateNewBinder();
        }
        private void BinderImageAnimate() {
            if (imagefullscreen) {
                AnimateImage.Fill = Brushes.Black;
                EventImage.BeginAnimation(Image.OpacityProperty, new DoubleAnimation(0, 100, TimeSpan.FromSeconds(120)));
            }
        }

        #endregion

        #endregion

        private int getBinderByName(String s) {
            return binders.FindIndex(b => ((System.Windows.Controls.Label)b.Children[1]).Content.ToString() == s);
            //return i;
        }
        // /*
        private void thread() {
            TCPServer.start();
            var timer = new System.Timers.Timer();
            timer.Elapsed += new System.Timers.ElapsedEventHandler(OnTimedEvent);
            timer.Interval = 180000;
            while (true) {
                if (TCPServer.accept()) {
                    //if (!TCPServer.isOcuppied()) {
                    this.Dispatcher.Invoke((Action)(() => {
                        ServerInfoLabel.Content = "Ocupado";
                        var temp = TCPServer.receive();
                        Console.WriteLine(temp);
                        if (temp == "handshake") {
                            if (TCPServer.InUse == false) {
                                TCPServer.send("hi");
                                timer.Enabled = true;
                                TCPServer.InUse = true;
                            } else {
                                TCPServer.send("occupied");
                                timer.Enabled = false;
                            }
                        } else if(temp=="exit"){
                            TCPServer.InUse = false;
                        } else if (Binder.Children.Count > 1) {
                            if (temp == "left") {
                                selectedBinderIndex--;
                                BinderImageAnimate();
                            } else if (temp == "right") {
                                selectedBinderIndex++;
                                BinderImageAnimate();
                            } else if (temp == "enter") {
                                if (imagefullscreen) {
                                    AnimateImage.Fill = Brushes.White;
                                    DoubleAnimation anim = new DoubleAnimation(0, 100, TimeSpan.FromSeconds(120));
                                    anim.Completed += (s, a) => AnimateImage.Fill = Brushes.Black;
                                    EventImage.BeginAnimation(Image.OpacityProperty, anim);
                                }
                            } else if (temp.Length > 6) {
                                if (temp.Substring(0, 7) == "select_") {
                                    selectedBinderIndex = Int32.Parse(temp.Substring(7)) + 1;
                                    BinderImageAnimate();
                                } else if (temp.Substring(0, 13) == "selectbyname_") {
                                    selectedBinderIndex = getBinderByName(temp.Substring(13)) + 1;
                                    BinderImageAnimate();
                                }

                            }

                        }
                        TCPServer.closeClient();
                        ServerInfoLabel.Content = "Conectado";
                    }));
                    //} else {
                    //    TCPServer.send("ocuppied");
                    //}
                }
            }
        }

        //*/
        /*
            private void thread() {
                TCPServer.start();
                while (true) {
                    if (TCPServer.accept()) {
                        this.Dispatcher.Invoke((Action)(() => {
                            var temp = TCPServer.receive();
                            Console.WriteLine(temp);
                            if (temp == "handshake") {
                                TCPServer.send("hi");
                            } else if (temp.Length > 7) {
                                if (temp.Substring(0, 8) == "nrequest") {
                                    var t = (System.Windows.Controls.Label)binders[Int32.Parse(temp.Substring(9))].Children[1];
                                    TCPServer.send(t.Content.ToString());
                                }
                                if (temp.Substring(0, 7) == "request") {
                                    if (temp.Length >= 13) {
                                        if (temp.Substring(0, 13) == "request_count") {
                                            TCPServer.send(binders.Count.ToString());
                                        }
                                    } else {
                                        int request = Int32.Parse(temp.Substring(8));
                                        var img = (Image)binders[request].Children[0];
                                        BitmapImage i = new BitmapImage();
                                        i.BeginInit();
                                        i.UriSource = new Uri(img.Source.ToString());
                                        i.EndInit();
                                        TCPServer.send(imgToByteArray(i));
                                    }
                                }
                            }
                            if (Binder.Children.Count > 1) {
                                if (temp == "left") {
                                    selectedBinderIndex--;
                                    BinderImageAnimate();
                                } else if (temp == "right") {
                                    selectedBinderIndex++;
                                    BinderImageAnimate();
                                } else if (temp == "enter") {
                                    GenericEvent();
                                } else if (temp.Substring(0, 4) == "set_") {
                                    selectedBinderIndex = Int32.Parse(temp.Substring(4));
                                }
                            }
                        }));
                        TCPServer.closeClient();
                    }
                }
            }
            */

        private byte[] imgToByteArray(BitmapImage image) {
            JpegBitmapEncoder enc = new JpegBitmapEncoder();
            enc.Frames.Add(BitmapFrame.Create(image));
            using (var ms = new MemoryStream()) {
                enc.Save(ms);
                return ms.ToArray();
            }
        }

        private void OnTimedEvent(object sender, System.Timers.ElapsedEventArgs e) {
            TCPServer.InUse = false;
        }

        private void MainWindowView_Closing(object sender, System.ComponentModel.CancelEventArgs e) {
            TCPServer.close();
        }
    }
}


