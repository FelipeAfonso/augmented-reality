using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Shapes;
using System.Xml;


namespace ProjectionTest {
    class ARMap {
        public string Name { get; set; }

        public DateTime Created_date { get; set; }

        public List<FrameworkElement> Elements { get; set; }
        
        public ARMap(string path) {
            Elements = new List<FrameworkElement>();
            try {
                XmlDocument doc = new XmlDocument();
                doc.Load(path);
                Name = doc.DocumentElement.SelectSingleNode("/Map/Name").InnerText;
                Created_date = DateTime.Parse(doc.DocumentElement.SelectSingleNode("/Map/Created_Date").InnerText);
                foreach (XmlNode c in doc.DocumentElement.SelectNodes("/Map/Controls/Control")) {
                    if (c.ChildNodes[0].InnerText == "System.Windows.Shapes.Ellipse") {
                        Ellipse e = new Ellipse();
                        e.Width = Convert.ToDouble(c.ChildNodes[1].InnerText) * 2;
                        e.Height = Convert.ToDouble(c.ChildNodes[1].InnerText) * 2;
                        e.Margin = new System.Windows.Thickness(Convert.ToDouble(c.ChildNodes[2].InnerText), Convert.ToDouble(c.ChildNodes[3].InnerText), 0, 0);
                        e.Fill = new SolidColorBrush((Color)ColorConverter.ConvertFromString(c.ChildNodes[4].InnerText));
                        Elements.Add(e);
                    } else if (c.ChildNodes[0].InnerText == "System.Windows.Shapes.Rectangle") {
                        Rectangle e = new Rectangle();
                        e.Width = Convert.ToDouble(c.ChildNodes[1].InnerText) * 2;
                        e.Height = Convert.ToDouble(c.ChildNodes[1].InnerText) * 2;
                        e.Margin = new System.Windows.Thickness(Convert.ToDouble(c.ChildNodes[2].InnerText), Convert.ToDouble(c.ChildNodes[3].InnerText), 0, 0);
                        e.Fill = new SolidColorBrush((Color)ColorConverter.ConvertFromString(c.ChildNodes[4].InnerText));
                        Elements.Add(e);
                    } else if (c.ChildNodes[0].InnerText == "System.Windows.Controls.Textbox") {
                        TextBox e = new TextBox();
                        e.Background = null;
                        e.SelectionBrush = Brushes.White;
                        e.BorderBrush = null;
                        e.TextWrapping = TextWrapping.Wrap;
                        e.Text = c.ChildNodes[1].InnerText;
                        e.Foreground = new SolidColorBrush((Color)ColorConverter.ConvertFromString(c.ChildNodes[2].InnerText));
                        e.Margin = new System.Windows.Thickness(Convert.ToDouble(c.ChildNodes[3].InnerText), Convert.ToDouble(c.ChildNodes[4].InnerText), 0, 0);
                        e.FontSize = Convert.ToDouble(c.ChildNodes[5].InnerText);
                        e.FontFamily = new FontFamily(c.ChildNodes[6].InnerText);
                    }

                }
            } catch {
                //Falha ao ler arquivo
            }
        }

    }
}
