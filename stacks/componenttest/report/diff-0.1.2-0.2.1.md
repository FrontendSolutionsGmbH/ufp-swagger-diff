## Version 0.1.2 to 0.2.1
---
### What's New
---

### What's Deprecated
---
* `POST` /pet/{petId}/uploadImage uploads an image
* `GET` /store/inventory Returns pet inventories by status
* `POST` /store/order Place an order for a pet
* `GET` /store/order/{orderId} Find purchase order by ID
* `DELETE` /store/order/{orderId} Delete purchase order by ID
* `POST` /user Create user
* `POST` /user/createWithArray Creates list of users with given input array
* `POST` /user/createWithList Creates list of users with given input array
* `GET` /user/login Logs user into the system
* `GET` /user/logout Logs out current logged in user session
* `GET` /user/{username} Get user by user name
* `PUT` /user/{username} Updated user
* `DELETE` /user/{username} Delete user

### What's Changed
---
`POST` /pet Add a new pet to the store  
    Parameters

        Delete body.id
        Modify body.photoUrls
`PUT` /pet Update an existing pet  
    Parameters

        Delete body.id
        Modify body.photoUrls
`GET` /pet/{petId} Find pet by ID  
    Return Type

        Delete id
        Modify photoUrls
