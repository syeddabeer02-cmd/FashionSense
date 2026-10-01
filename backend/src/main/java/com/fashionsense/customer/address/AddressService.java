package com.fashionsense.customer.address;

import com.fashionsense.customer.User;
import com.fashionsense.customer.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(
            AddressRepository addressRepository,
            UserRepository userRepository
    ) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getAddresses(
            Long userId
    ) {

        return addressRepository
                .findByUserIdOrderByIdAsc(userId)
                .stream()
                .map(AddressResponse::from)
                .toList();
    }

    @Transactional
    public AddressResponse createAddress(
            Long userId,
            AddressRequest request
    ) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new AddressNotFoundException(
                                        "Customer not found"
                                )
                        );

        boolean noDefaultExists =
                addressRepository
                        .findByUserIdAndDefaultAddressTrue(userId)
                        .isEmpty();

        boolean makeDefault =
                request.defaultAddress()
                        || noDefaultExists;

        if (makeDefault) {
            addressRepository
                    .clearDefaultAddress(userId);
        }

        Address address = new Address();

        address.setUser(user);
        address.setLabel(request.label());
        address.setRecipientName(
                request.recipientName().trim()
        );
        address.setAddressLine1(
                request.addressLine1().trim()
        );
        address.setAddressLine2(
                normalizeOptional(
                        request.addressLine2()
                )
        );
        address.setCity(
                request.city().trim()
        );
        address.setState(
                request.state().trim()
        );
        address.setPostalCode(
                request.postalCode().trim()
        );
        address.setCountryCode(
                request.countryCode()
                        .trim()
                        .toUpperCase()
        );
        address.setPhone(
                normalizeOptional(
                        request.phone()
                )
        );
        address.setDefaultAddress(
                makeDefault
        );

        return AddressResponse.from(
                addressRepository.save(address)
        );
    }

    @Transactional
    public AddressResponse setDefaultAddress(
            Long userId,
            Long addressId
    ) {

        Address address =
                addressRepository
                        .findByIdAndUserId(
                                addressId,
                                userId
                        )
                        .orElseThrow(() ->
                                new AddressNotFoundException(
                                        "Address not found with id: "
                                                + addressId
                                )
                        );

        addressRepository
                .clearDefaultAddress(userId);

        address.setDefaultAddress(true);

        return AddressResponse.from(
                addressRepository.save(address)
        );
    }

    @Transactional
    public void deleteAddress(
            Long userId,
            Long addressId
    ) {

        Address address =
                addressRepository
                        .findByIdAndUserId(
                                addressId,
                                userId
                        )
                        .orElseThrow(() ->
                                new AddressNotFoundException(
                                        "Address not found with id: "
                                                + addressId
                                )
                        );

        boolean wasDefault =
                address.isDefaultAddress();

        addressRepository.delete(address);
        addressRepository.flush();

        if (wasDefault) {

            List<Address> remaining =
                    addressRepository
                            .findByUserIdOrderByIdAsc(
                                    userId
                            );

            if (!remaining.isEmpty()) {

                Address newDefault =
                        remaining.getFirst();

                newDefault.setDefaultAddress(true);

                addressRepository.save(newDefault);
            }
        }
    }

    private String normalizeOptional(
            String value
    ) {

        if (value == null) {
            return null;
        }

        String trimmed = value.trim();

        return trimmed.isEmpty()
                ? null
                : trimmed;
    }
}