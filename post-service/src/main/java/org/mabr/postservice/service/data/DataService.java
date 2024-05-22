package org.mabr.postservice.service.data;

import lombok.RequiredArgsConstructor;
import org.mabr.postservice.entity.data.*;
import org.mabr.postservice.exception.ResourceNotFoundException;
import org.mabr.postservice.repository.data.*;
import org.mabr.postservice.util.ImageUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DataService {

    private final CurrencyRepository currencyRepository;
    private final CityRepository cityRepository;
    private final ImageRepository imageRepository;
    private final LabelRepository labelRepository;
    private final TempImageRepository tempImageRepository;

    public List<Currency> getCurrencyList() {
        return currencyRepository.findAll();
    }

    public List<City> getCityList() {
        return cityRepository.findAll();
    }

    public Currency getCurrency(int currencyId) {
        return currencyRepository.findById(currencyId)
                .orElseThrow(() -> new ResourceNotFoundException("Currency", "id", String.valueOf(currencyId)));
    }

    public City getCity(int cityId) {
        return cityRepository.findById(cityId)
                .orElseThrow(() -> new ResourceNotFoundException("City", "id", String.valueOf(cityId)));
    }

    public Image saveImage(String imageLink) {
        var tempImage = tempImageRepository.findByUrl(imageLink)
                .orElseThrow(() -> new ResourceNotFoundException("image", "url", imageLink));

        var image = Image.builder()
                .code(tempImage.getCode())
                .name(tempImage.getName())
                .type(tempImage.getType())
                .data(tempImage.getData())
                .url(tempImage.getUrl())
                .build();

        tempImageRepository.delete(tempImage);

        return imageRepository.save(image);
    }

    public Image saveImage(MultipartFile file) {
        try {
            var code = UUID.randomUUID().toString();

            var image = Image.builder()
                    .code(code)
                    .name(file.getOriginalFilename())
                    .type(file.getContentType())
                    .data(ImageUtil.compress(file))
                    .url(createImageLink(code))
                    .build();

            return imageRepository.save(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO: разобраться с необходимостью временных изображений
    public TempImage uploadTempImage(MultipartFile file) {
        try {
            var code = UUID.randomUUID().toString();

            var image = TempImage.builder()
                    .code(code)
                    .name(file.getOriginalFilename())
                    .type(file.getContentType())
                    .data(ImageUtil.compress(file))
                    .url(createImageLink(code))
                    .build();

            return tempImageRepository.save(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Image getImage(String code) {
        var image = imageRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("image", "code", code));

        image.setData(image.getData());

        return image;
    }

    public void deleteImage(int id) {
        imageRepository.deleteById(id);
    }

    public String createImageLink(String code) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .replacePath("/api/data/image/" + code)
                .toUriString();
    }

    public Set<Label> getLabelListById(Iterable<Integer> ids) {
        var labels = new HashSet<Label>();

        for (var id : ids) {
            var label = labelRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("label", "id", String.valueOf(id)));

            labels.add(label);
        }

        return labels;
    }
}
