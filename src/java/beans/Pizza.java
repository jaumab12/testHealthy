/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

/**
 *
 * @author Lukas
 */
public class Pizza
{
    private String name;
    private double price;
    private String imageString;

    public Pizza()
    {
    }

    public Pizza(String name, double price, String imageString)
    {
        this.name = name;
        this.price = price;
        this.imageString = imageString;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public double getPrice()
    {
        return price;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

    public String getImageString()
    {
        return imageString;
    }

    public void setImageString(String imageString)
    {
        this.imageString = imageString;
    }
    
    
}
