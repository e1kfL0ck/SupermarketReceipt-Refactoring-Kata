# Changes

## TODO ?
- ReceiptItem renamed in ShoppingItem ?
- Remove totalPrice in Recipet item
## Tests



## Code refactoring

### Class addeds
- ReceiptDisplay class to display the receipt in either a console or a web view
    - printReceipt method to print the receipt in the console
    - generateReceiptHTML method to generate the HTML code of the receipt for web view
    - It was easier to implement the display of the receipt in a separate class rather than in the Receipt class, it also make more sense from a design point of view
- CheckoutCounter class to manage the checkout process
    - checkout method to handle the checkout process, it takes a ShoppingCart and a List of Offer as parameters, this change has been made to separate the checkout process from the ShoppingCart class
    - It contains a Map of product and qty to be sure to iterate over each product (only EACH product, not kilo) along different offers
    - The checkout is now iterating over Bundle then regular offers then kilo products to avoid issues with offers
- Coupon class to manage the coupon offers, it's very similar to Discount and Offer class but it's better to differentiate them

### Class deletions
- Remove productQuantity class, the quantity is an attribute of RecieptItem class
- Interface SuperMarketCatalog has been removed, the catalog of offer is now a List<Offer> in ShoppingCart class, this change was made to simplify the code and avoid unnecessary complexity
- Teller class has been removed, the methods are now in ShoppingCart class
    - checksOutArticlesFrom method was responsible for adding element in the receipt, now it is in ShoppingCart class. It was also responsible for handling offers, now it is in ShoppingCart class, in a dedicated method handleOffer

### Class edited
- Offer class has been change to manage different kind of offers
    -  It contains List<Product> products to manage the products associated with the offer "Bundle"
    - It contains the offerType and an argument which is the amount of this offer
- Dicount class contains List<Product> products to manage the products associated with the offer "Bundle"
- Product class now contains an attribute "price" to store the price of a product
- RecieptItem class does not store the price of an item anymore, the price as it is now an attribute of Product class
- ShoppingCart Contains a map of Product Offer for faster access to the offers when checking out articles
- Receipt class now contains attibute totalPrice and totalDiscount. It also contains method pay() to calculate the total price and total discount of the receipt.

### Temporary changes (ie. changes that are not final, but were needed for the refactoring)
- DefaultOffer class has been created to manage the different offers. Offers and BundleOffers were herited from this class. This class is now removed and everything is in Offer class.
- ShoppingCart class now contains a List<Offer> to manage the offers of the supermarket
    - handleOffer method has been completely reworked into smallers methods. The structure is now a switch case on the offerType. It could use some improvements to avoid code duplications.
- testSupermarketMethods class to test methods of ShoppingCart class


## Further improvements
- The Discount and Offer class could be merged into one class
